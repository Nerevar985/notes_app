package com.kanush_productions.shoppinglist.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.kanush_productions.shoppinglist.R
import com.kanush_productions.shoppinglist.databinding.ActivityNewNoteBinding
import com.kanush_productions.shoppinglist.entities.NoteItems
import com.kanush_productions.shoppinglist.fragments.NoteFragment
import com.kanush_productions.shoppinglist.utils.HtmlManager
import com.kanush_productions.shoppinglist.utils.MyTouchListener
import com.kanush_productions.shoppinglist.utils.TimeManager


class NewNoteActivity : AppCompatActivity() {
    private lateinit var bind: ActivityNewNoteBinding
    private var note: NoteItems? = null
    private var pref: SharedPreferences? = null
    private lateinit var defPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        bind = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(bind.root)
        //setSupportActionBar(bind.tool)
//        supportActionBar?.title = "New Note"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
        getNote()
        setTextSize()
        onClickColorPicker()
    }

    private fun onClickColorPicker() = with(bind){
        ibRed.setOnClickListener {
            setColorForSelectedText(R.color.red)
        }
        ibBlack.setOnClickListener {
            setColorForSelectedText(R.color.black_picker)
        }
        ibBlue.setOnClickListener {
            setColorForSelectedText(R.color.blue_picker)
        }
        ibGreen.setOnClickListener {
            setColorForSelectedText(R.color.green)
        }
        ibOrange.setOnClickListener {
            setColorForSelectedText(R.color.orange)
        }
        ibPurple.setOnClickListener {
            setColorForSelectedText(R.color.purple)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        bind.llColorPicker.setOnTouchListener(MyTouchListener())
        pref = PreferenceManager.getDefaultSharedPreferences(this)
    }
    private fun getNote() {
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->{
                val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY, NoteItems::class.java)
                //note = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY, NoteItems::class.java)
                if (sNote != null) {
                    note = sNote
                    supportActionBar?.title = note!!.title
                    fillNote()
                }
            }
            else -> {
                @Suppress("DEPRECATION")
                val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
                if(sNote != null) {
                    note = sNote as NoteItems
                    fillNote()
                }
            }
        }
    }
    private fun fillNote() = with(bind){
        etTitle.setText(note?.title)
        etDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                setMainResult()
            }
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
            }
            R.id.menu_bold -> {
                setBoldForSelectedText()
            }
            R.id.color_picker -> {
                if(bind.llColorPicker.isShown){
                    closeColorPicker()
                } else{
                    openColorPicker()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText() = with(bind){
        val startPos = etDescription.selectionStart
        val endPos = etDescription.selectionEnd
        val styles = etDescription.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if(styles.isNotEmpty()){
            etDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }
        etDescription.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        etDescription.text.trim()
        etDescription.setSelection(startPos)
    }

    private fun setColorForSelectedText(colorId: Int) = with(bind){
        val startPos = etDescription.selectionStart
        val endPos = etDescription.selectionEnd

        val styles = etDescription.text.getSpans(startPos, endPos, ForegroundColorSpan::class.java)
        if(styles.isNotEmpty()) etDescription.text.removeSpan(styles[0])

        etDescription.text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@NewNoteActivity, colorId)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        etDescription.text.trim()
        etDescription.setSelection(startPos)
    }

    private fun setMainResult(){
        var editState = "new"
        val tempNote: NoteItems? = if(note == null) {
            createNewNote()
        }else {
            editState = "update"
            updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, i)
        finish()
    }
    private fun updateNote(): NoteItems? = with(bind){
        return note?.copy(
            title = etTitle.text.toString(),
            content = HtmlManager.toHtml(etDescription.text))
    }

    private fun createNewNote():NoteItems{
        return NoteItems(
            null,
            bind.etTitle.text.toString(),
            HtmlManager.toHtml(bind.etDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }
    private fun openColorPicker(){
        bind.llColorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        bind.llColorPicker.startAnimation(openAnim)
    }
    private fun closeColorPicker(){
        val closeAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        closeAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                bind.llColorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        bind.llColorPicker.startAnimation(closeAnim)
    }

    private fun setTextSize() = with(bind){
        etTitle.setTextSize(pref?.getString("title_text_size_key", "18"))
        etDescription.setTextSize(pref?.getString("note_text_size_key", "16"))
    }

    private fun EditText.setTextSize(size: String?){
        if (size != null) this.textSize = size.toFloat()
    }

    private fun getSelectedTheme():Int{
        return if (defPref.getString("theme_key", "Light") == "Light") {
            R.style.MyMainStyle
        } else {
            R.style.MyMainStyleDark
        }
    }

}