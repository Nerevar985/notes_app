package com.kanush_productions.shoppinglist.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kanush_productions.shoppinglist.activities.MainApp
import com.kanush_productions.shoppinglist.activities.NewNoteActivity
import com.kanush_productions.shoppinglist.database.MainViewModel
import com.kanush_productions.shoppinglist.database.NoteAdapter
import com.kanush_productions.shoppinglist.databinding.FragmentNoteBinding
import com.kanush_productions.shoppinglist.dialogs.DeleteDialog
import com.kanush_productions.shoppinglist.entities.NoteItems
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.common.AdRequest


class NoteFragment : BaseFragment(), NoteAdapter.Listener {
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var bind: FragmentNoteBinding
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).dataBase)
    }
    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewNoteActivity::class.java))
    }

    private fun fabListener(){
        bind.fab.setOnClickListener {
            onClickNew()
        }
    }

    private fun RecyclerView.attachFab(fab : FloatingActionButton) {
        this.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.isShown)
                    fab.hide()
                else if (dy < 0 && !fab.isShown)
                    fab.show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentNoteBinding.inflate(inflater, container, false)
        bind.banner.setAdUnitId("R-M-2492467-2")
        bind.banner.setAdSize(AdSize.stickySize(350))
        val adRequest = AdRequest.Builder().build()
        //bind.banner.loadAd(adRequest)

        return bind.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
        fabListener()
        bind.rvNoteview.attachFab(bind.fab)
    }

    private fun onEditResult(){
        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == Activity.RESULT_OK) {
                val editState = it.data?.getStringExtra(EDIT_STATE_KEY)
                if(editState == "update"){
                    when{
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                            mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY, NoteItems::class.java)!!)
                        else -> @Suppress("DEPRECATION") mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItems)
                    }
                }else {
                    when{
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                            mainViewModel.insertNote(it.data?.getSerializableExtra("new_note_key", NoteItems::class.java)!!)
                        else -> @Suppress("DEPRECATION") mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItems)
                    }
                }

            }
        }
    }
    private fun initRcView() = with(bind){
        defPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        rvNoteview.layoutManager = getLayoutManager()
        adapter = NoteAdapter(this@NoteFragment, defPref)
        rvNoteview.adapter = adapter
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager{
        return if (defPref.getString("note_style_key", "Grid") == "Linear") LinearLayoutManager(activity)
        else StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun observer() {
        mainViewModel.allNotes.observe(viewLifecycleOwner) {
            adapter.submitList(it)

            if (it.isEmpty()) bind.emptyNotes.visibility = View.VISIBLE
            else bind.emptyNotes.visibility = View.GONE
        }
    }

    companion object {
        const val NEW_NOTE_KEY = "new_note_key"
        const val EDIT_STATE_KEY = "edit_state_key"
        @JvmStatic
        fun newInstance() = NoteFragment()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object: DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteNote(id)
            }
        })

    }

    override fun onClickItem(note: NoteItems) {
        val intent = Intent(activity, NewNoteActivity::class.java).apply {
            putExtra(NEW_NOTE_KEY, note)

        }
        editLauncher.launch(intent)
    }
}