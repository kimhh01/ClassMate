package com.example.navermapsample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class CardviewAdapter(private val buttonClickListener: (Int) -> Unit) : RecyclerView.Adapter<CardviewAdapter.CardViewHolder>() {

    private val buttonImages = arrayOf(
        R.drawable.homepage,  // 첫 번째 버튼 이미지
        R.drawable.instargram,  // 두 번째 버튼 이미지
        R.drawable.facebook,  // 세 번째 버튼 이미지
        R.drawable.blog,  // 네 번째 버튼 이미지
        R.drawable.youtube   // 다섯 번째 버튼 이미지
    )

    override fun getItemCount(): Int = buttonImages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.cardButton.setImageResource(buttonImages[position])  // 각 버튼의 이미지 설정

        holder.cardButton.setOnClickListener {
            buttonClickListener(position)  // 클릭 시 위치 정보 전달
        }
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardButton: ImageButton = itemView.findViewById(R.id.cardButton)
    }
}

