package com.adrian.segmentablestepsview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isAdd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSwitch.setOnClickListener {
            segmentableStepsView.stepStyle = when (segmentableStepsView.stepStyle) {
                SegmentableStepsView.STYLE_LINE_HORIZONTAL -> SegmentableStepsView.STYLE_LINE_VERTICAL
                SegmentableStepsView.STYLE_LINE_VERTICAL -> SegmentableStepsView.STYLE_RING
                SegmentableStepsView.STYLE_RING -> SegmentableStepsView.STYLE_CIRCLE
                else -> SegmentableStepsView.STYLE_LINE_HORIZONTAL
            }
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                "Step".logE("current progress:$progress")
                isAdd = progress > segmentableStepsView.stepIndex
                segmentableStepsView.stepIndex = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        segmentableStepsView.maxSteps = seekbar.max
        segmentableStepsView.stepIndex = seekbar.progress
        segmentableStepsView.stepChangeListener = { step ->
            "Step".logE("current step:$step")
            if (step == 0) {
                segmentableStepsView.ringCenterText = "步骤(0)"
            } else {
                if (isAdd) {
                    segmentableStepsView.ringCenterText += "步骤(${step})"
                } else {
                    val string = segmentableStepsView.ringCenterText
                    val index = string?.indexOf("步骤(${step+1})".also { "Data".logE("delete content:$it") }).also { "Data".logE("delete index:$it") }
                    if (index!! > 0) {
                        segmentableStepsView.ringCenterText = string?.substring(0, index)
                    }
                }
            }
        }
        btnIncrease.setOnClickListener {
            isAdd = true
            seekbar.progress++
        }
        btnDecrease.setOnClickListener {
            isAdd = false
            seekbar.progress--
        }
        seekbar.progress = 0
    }
}