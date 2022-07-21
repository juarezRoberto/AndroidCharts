package com.example.androidcharts

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidcharts.databinding.ItemAnswerBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

class AnswerAdapter : ListAdapter<AnswerWrapper, AnswerAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val answer = getItem(position)

        with(holder.binding) {
            txtQuestion.text = answer.question
            txtTotalAnswers.text = "${String.format("%,d", answer.totalAnswers)} respuestas"
        }

        if (answer.type == AnswerType.PieChart || answer.type == AnswerType.BarChart) {
            val footerAdapter = FooterChartAdapter()
            with(holder) {
                binding.chartFooterRecycler.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = footerAdapter
                }

                answer.answers.mapIndexed { index, ans ->
                    FooterChart(index, ans.total, ans.answer)
                }.also { footerList ->
                    footerAdapter.submitList(footerList)
                }


                if (answer.type == AnswerType.PieChart) {
                    onPieAnswerType(binding, answer.answers)
                } else {
                    onBarAnswerType(binding, answer.answers)
                }
            }
        }

        if (answer.type == AnswerType.Words) {

        }

        if (answer.type == AnswerType.Photos) {

        }

        if (answer.type == AnswerType.Videos) {

        }
    }

    private fun onPieAnswerType(binding: ItemAnswerBinding, answers: List<Answer>) {

        binding.itemBarChart.isVisible = false
        binding.itemPieChart.apply {
            this.isVisible = true
            this.setTouchEnabled(false)
            this.setUsePercentValues(true)
            this.description = null
            this.setExtraOffsets(5f, 10f, 5f, 5f)
            this.dragDecelerationFrictionCoef = 0.95f
            this.isDrawHoleEnabled = false
            this.legend.isEnabled = false
        }

        val entries = answers.map { answer -> PieEntry(answer.total.toFloat()) }

        val pieDataSet = PieDataSet(entries, "PieChart").apply {
            this.sliceSpace = 0f
            this.selectionShift = 30f
            this.colors = Utils.getColors(binding.root.context)
            this.valueLinePart1OffsetPercentage = 100f
            this.valueLinePart1Length = 0.7f
            this.valueLinePart2Length = 0.3f
            this.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val pieData = PieData(pieDataSet).apply {
            this.setValueTextSize(14f)
            this.setValueTextColor(Color.GRAY)
            this.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}%"
                }
            })
        }

        binding.itemPieChart.apply {
            this.data = pieData
            this.invalidate()
        }
    }

    private fun onBarAnswerType(binding: ItemAnswerBinding, answers: List<Answer>) {

        binding.itemPieChart.isVisible = false
        binding.itemBarChart.apply {
            this.isVisible = true
            this.setTouchEnabled(false)
            this.setPinchZoom(false)
            this.setDrawBarShadow(false)
            this.setDrawGridBackground(false)
            this.description = null
            this.axisLeft.apply {
                this.isEnabled = true
                this.textColor = Color.GRAY
                this.textSize = 12f
                this.labelCount = 5
                this.axisMinimum = 0f
            }
            this.axisLeft.setDrawAxisLine(false)
            this.axisRight.isEnabled = false
            this.legend.isEnabled = false
            this.xAxis.apply {
                this.position = XAxis.XAxisPosition.BOTTOM
                this.setDrawAxisLine(false)
                this.setDrawGridLines(false)
                this.textSize = 12f
                this.textColor = Color.GRAY
                this.labelCount = answers.size
                this.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val answerFound = answers.find { it.id.toFloat() == value }
                        if (answerFound == null) return "0%"
                        return "${answerFound.percent}%"
                    }
                }
            }
        }

        val entries =
            answers.map { answer -> BarEntry(answer.id.toFloat(), answer.total.toFloat()) }

        val dataset = BarDataSet(entries, "BarChart").apply {
            this.colors = Utils.getColors(binding.root.context)
            this.setDrawValues(false)
        }

        val data = BarData(listOf<IBarDataSet>(dataset)).apply {
            this.barWidth = 0.5f
        }

        binding.itemBarChart.apply {
            this.data = data
            this.setFitBars(true)
            this.invalidate()
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<AnswerWrapper>() {
        override fun areItemsTheSame(oldItem: AnswerWrapper, newItem: AnswerWrapper) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AnswerWrapper, newItem: AnswerWrapper) =
            oldItem == newItem
    }
}