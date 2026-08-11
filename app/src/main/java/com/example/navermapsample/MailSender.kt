package com.example.navermapsample;

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class MailSender(private val context: Context) {

    private val senderEmail = "khs10049731@gmail.com"
    private val senderPassword = "gbes whfi ykhc bkpd"

    fun sendEmail(
        recipient: String,
        subject: String,
        message: String,
        message2: String,
        attachmentUris: List<Uri>?
    ) {
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        // 🔹 로딩 애니메이션 다이얼로그 표시
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_lottie_loading, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // 배경 투명 처리
        dialog.show()

        // 🔹 애니메이션 시작
        val lottieAnimationView = dialogView.findViewById<LottieAnimationView>(R.id.lottie_loading)
        lottieAnimationView.playAnimation()

        Thread {
            try {
                val mimeMessage = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
                    this.subject = subject

                    val multipart = MimeMultipart()

                    // 🔹 메일 본문 추가
                    val textBodyPart = MimeBodyPart().apply {
                        setText("$message\n\n$message2")
                    }
                    multipart.addBodyPart(textBodyPart)

                    // 🔹 이미지 첨부 파일 추가 (다중 파일 지원)
                    attachmentUris?.forEach { uri ->
                        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                        val attachmentBodyPart = MimeBodyPart()

                        inputStream?.use { stream ->
                            val tempFile = File(context.cacheDir, "attachment_${System.currentTimeMillis()}.jpg")
                            FileOutputStream(tempFile).use { output ->
                                stream.copyTo(output)
                            }
                            val dataSource: DataSource = FileDataSource(tempFile)
                            attachmentBodyPart.dataHandler = DataHandler(dataSource)
                            attachmentBodyPart.fileName = tempFile.name
                            multipart.addBodyPart(attachmentBodyPart)
                        }
                    }

                    setContent(multipart)
                }

                Transport.send(mimeMessage)

                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(context, "메일이 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(context, "메일 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // 🔹 로딩 다이얼로그 닫기
                (context as? Activity)?.runOnUiThread {
                    dialog.dismiss()
                }
            }
        }.start()
    }
}
