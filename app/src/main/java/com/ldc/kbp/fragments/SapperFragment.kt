package com.ldc.kbp.fragments

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ldc.kbp.R
import com.ldc.kbp.config
import com.ldc.kbp.models.Files
import kotlinx.android.synthetic.main.fragment_sapper.view.*

class SapperFragment : Fragment() {

    private var sx = config.sapperSizeX
    private var sy = config.sapperSizeY
    private var minesCount = config.sapperMinesCount
    private lateinit var field: MutableList<MutableList<Int>>
    private var buttons = mutableListOf<MutableList<Button>>()

    private var openedCellsCount = 0
    private var flagsCount = 0
    private var isFirstMove = true
    private var isGame = true

    private val flag = "\uD83C\uDFF4"
    private val mine = "\uD83C\uDFAF"

    lateinit var mainView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(inflater.inflate(R.layout.fragment_sapper, container, false)) {
            mainView = this

            restart_btn.isVisible = false
            settings_lt.isVisible = false
            restart_head_btn.setOnClickListener { restart() }
            restart_btn.setOnClickListener { restart() }

            restart_settings_btn.setOnClickListener {
                sy = sy_et.text.toString().toIntOrNull() ?: 5
                sx = sx_et.text.toString().toIntOrNull() ?: 5
                minesCount = mines_et.text.toString().toIntOrNull() ?: 5

                if (sx < 6) sx = 6
                if (sy < 6) sy = 6

                if (sx > 50) sx = 50
                if (sy > 50) sy = 50

                if (minesCount > sx * sy - 30) minesCount = sx * sy - 30
                if (minesCount < 5) minesCount = 5

                config.sapperSizeX = sx
                config.sapperSizeY = sy
                config.sapperMinesCount = minesCount

                Files.saveConfig(requireContext())

                settings_lt.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(), R.anim.anim_link_select_cancel
                    )
                )
                settings_lt.isVisible = false

                restart()
            }
            roll_up.setOnClickListener {
                settings_lt.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(), R.anim.anim_link_select_cancel
                    )
                )
                settings_lt.isVisible = false
            }

            mainView.cell_left_tv.text = "Открыто: $openedCellsCount/${sx * sy - minesCount}"
            mainView.flag_stats_tv.text = "$flag: $flagsCount/$mine: $minesCount"

            settings_img.setOnClickListener {
                sy_et.setText(sx.toString())
                sx_et.setText(sy.toString())
                mines_et.setText(minesCount.toString())

                settings_lt.isVisible = true
                settings_lt.startAnimation(
                    AnimationUtils.loadAnimation(
                        context, R.anim.anim_link_select_show
                    )
                )
            }

            restart()

            return this
        }
    }

    private fun initField(x: Int, y: Int) {
        isFirstMove = false

        field = MutableList(sx) { MutableList(sy) { 1 } }

        for (i in 0 until minesCount) {
            var rx: Int
            var ry: Int

            do {
                rx = (0 until sx).random()
                ry = (0 until sy).random()
            } while (field[rx][ry] != 1 || (rx in (x - 2)..(x + 2) && ry in (y - 2)..(y + 2)))

            field[rx][ry] = 2
        }
    }

    private fun getCellValue(x: Int, y: Int): String {
        var near = 0

        if (field[x][y] == 2) return "\uD83C\uDFAF"

        if (isFieldInBounds(x - 1, y - 1) && field[x - 1][y - 1] == 2) near++
        if (isFieldInBounds(x, y - 1) && field[x][y - 1] == 2) near++
        if (isFieldInBounds(x + 1, y + 1) && field[x + 1][y + 1] == 2) near++
        if (isFieldInBounds(x + 1, y) && field[x + 1][y] == 2) near++
        if (isFieldInBounds(x + 1, y - 1) && field[x + 1][y - 1] == 2) near++
        if (isFieldInBounds(x, y + 1) && field[x][y + 1] == 2) near++
        if (isFieldInBounds(x - 1, y + 1) && field[x - 1][y + 1] == 2) near++
        if (isFieldInBounds(x - 1, y) && field[x - 1][y] == 2) near++

        return if (near == 0) "" else "$near"
    }

    private fun open(x: Int, y: Int) {
        if (isFieldInBounds(x, y) && buttons[x][y].isEnabled && buttons[x][y].text != flag) {
            openedCellsCount++

            buttons[x][y].isEnabled = false
            buttons[x][y].text = getCellValue(x, y)

            if (buttons[x][y].text == mine) {
                mainView.restart_btn.isVisible = true
                isGame = false
            } else
                if (getCellValue(x, y) == "") {
                    open(x - 1, y - 1)
                    open(x, y - 1)
                    open(x + 1, y + 1)
                    open(x + 1, y)
                    open(x + 1, y - 1)
                    open(x, y + 1)
                    open(x - 1, y + 1)
                    open(x - 1, y)
                }
        }
    }

    private fun isFieldInBounds(x: Int, y: Int) = field.isInBounds(x) && field[x].isInBounds(y)
    private fun <E> MutableList<E>.isInBounds(i: Int) = i in 0 until size

    private fun restart() {
        mainView.restart_btn.isVisible = false
        isGame = true
        isFirstMove = true

        openedCellsCount = 0
        flagsCount = 0

        mainView.field_lt.removeAllViews()
        buttons = mutableListOf()

        for (ix in 0 until sx) {
            val lt = LinearLayout(requireContext())
            lt.orientation = LinearLayout.VERTICAL

            buttons.add(mutableListOf())

            for (iy in 0 until sy)
                lt.addView(layoutInflater.inflate(R.layout.button_sapper, lt, false).apply {
                    buttons[ix].add(this as Button)

                    setOnClickListener {
                        if (isGame && text != flag) {
                            if (isFirstMove) initField(ix, iy)

                            open(ix, iy)
                            mainView.cell_left_tv.text =
                                "Открыто: $openedCellsCount/${sx * sy - minesCount}"

                            if (openedCellsCount == sx * sy - minesCount) {
                                isGame = false
                                mainView.restart_btn.isVisible = true
                            }
                        }
                    }

                    setOnLongClickListener {
                        if (isGame) {
                            text = if (text == flag) {
                                flagsCount--
                                ""
                            } else {
                                flagsCount++
                                flag
                            }

                            mainView.flag_stats_tv.text =
                                "$flag: $flagsCount/$mine: $minesCount"

                            (requireActivity().getSystemService("vibrator") as Vibrator).vibrate(
                                VibrationEffect.createOneShot(
                                    75, VibrationEffect.CONTENTS_FILE_DESCRIPTOR
                                )
                            )
                        }

                        true
                    }
                })

            mainView.field_lt.addView(lt)
        }

        mainView.cell_left_tv.text =
            "Открыто: $openedCellsCount/${sx * sy - minesCount}"
        mainView.flag_stats_tv.text =
            "$flag: $flagsCount/$mine: $minesCount"
    }
}