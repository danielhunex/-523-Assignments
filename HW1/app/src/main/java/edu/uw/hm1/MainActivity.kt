package edu.uw.hm1

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var _letterSelected: String
    private lateinit var _currentCharStr: String
    private lateinit var _buttons: MutableList<Button>
    private lateinit var _textViews: MutableList<TextView>
    private var _textViewSelected: TextView? = null
    private var _btnSelected: Button? = null
    private var _word: Word = Word()

    private lateinit var _txtRandomWord: TextView
    private lateinit var _edTxtGuessWord: EditText
    private lateinit var _btnReset: Button
    private lateinit var _btnCheck: Button;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _txtRandomWord = findViewById<TextView>(R.id.txt_random_word)
        _edTxtGuessWord = findViewById<EditText>(R.id.et_guess_word)
        _btnReset = findViewById(R.id.btn_reset)
        _btnCheck = findViewById(R.id.btn_check)

        reset()
        _edTxtGuessWord.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (_btnCheck.text.toString() != "Check") {
                    _btnCheck.text = "Check!"
                    _btnCheck.setTextColor(Color.WHITE)
                    _btnCheck.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        _btnCheck.setOnClickListener() {
            if (_edTxtGuessWord.text.toString() == _letterSelected) {
                _btnCheck.text = "Correct!"
                _btnCheck.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                _btnCheck.setTextColor(Color.GREEN)
            } else {
                _btnCheck.text = "Wrong!"
                _btnCheck.setTextColor(Color.WHITE)
                _btnCheck.setBackgroundColor(Color.RED)
            }
        }

        _btnReset.setOnClickListener {
            reset()
        }
    }

    private fun reset() {
        _letterSelected = _word.getRandomWord();
        _txtRandomWord.text = getShuffledWord(_letterSelected)
        _edTxtGuessWord.text.clear()
        _btnCheck.text = "Check!"
        _btnCheck.setTextColor(Color.WHITE)
        _btnCheck.setBackgroundColor(resources.getColor(R.color.colorPrimary))
    }

    private fun getShuffledWord(input: String): String {
        var list = input.toCharArray().toMutableList().apply { }
        list.shuffle()
        return String(list.toCharArray())
    }

    private fun createTextViews(count: Int, layout: LinearLayout) {
        _textViews = mutableListOf()

        for (i in 1..count) {
            val txtView = TextView(this);
            txtView.layoutParams = LinearLayout.LayoutParams(80, WRAP_CONTENT)
            txtView.id = i
            txtView.text = _letterSelected[i - 1].toString()
            txtView.gravity = Gravity.CENTER
            txtView.setPadding(10, 20, 10, 20)

            val gd = GradientDrawable()
            gd.setColor(-0xff0100) // Changes this drawbale to use a single color instead of a gradient

            gd.cornerRadius = 5f
            gd.setStroke(1, -0x1000000)
            txtView.background = gd
            txtView.setOnClickListener {
                _currentCharStr = txtView.text.toString()
                _textViewSelected = txtView;
                txtView.text = " ";
            }
            _textViews.add(txtView);
            layout.addView(txtView)
        }
    }

    private fun createButtons(count: Int, layout: LinearLayout) {
        _buttons = mutableListOf()
        for (i in 1..count) {
            val btn = Button(this);
            btn.layoutParams = LinearLayout.LayoutParams(80, WRAP_CONTENT)
            btn.id = i;
            btn.text = " "
            btn.setOnClickListener {
                var text = btn.text;

                if (_btnSelected == null)


                    if (!_currentCharStr.isNullOrEmpty()) {
                        btn.text = _currentCharStr;
                        _currentCharStr = ""
                    } else if (!text.isNullOrEmpty()) {
                        _currentCharStr = text as String //TODO: Update the labels back
                    }
                _btnSelected = btn
            }

            _buttons.add(btn);
            layout.addView(btn)
        }
    }

}
