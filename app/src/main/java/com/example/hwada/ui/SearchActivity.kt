package com.example.hwada.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwada.Model.Ad
import com.example.hwada.Model.User
import com.example.hwada.R
import com.example.hwada.adapter.AdsAdapter
import com.example.hwada.databinding.ActivitySearchBinding
import com.example.hwada.ui.view.ad.AdvertiserFragment
import com.example.hwada.ui.view.main.HomeFragment
import com.example.hwada.util.Constants.SEARCH_DELAY
import com.example.hwada.viewmodel.FavViewModel
import com.example.hwada.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() , AdsAdapter.OnItemListener ,OnClickListener{

    private val DEBOUNCE_DELAY_MILLIS: Long = 500
    private var debouncing = false
    private var debounceRunnable: Runnable? = null
    private val debounceHandler = Handler()

    private lateinit var binding: ActivitySearchBinding;
    var list : ArrayList<Ad> = ArrayList()
    lateinit var user: User
    lateinit var adapter: AdsAdapter
    private val searchViewModel : SearchViewModel by viewModels()
    private val favViewModel : FavViewModel by viewModels()
    var advertiserFragment: AdvertiserFragment = AdvertiserFragment()

    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = intent.getParcelableExtra(getString(R.string.userVal))!!
        setListeners()
        setRecycler()
        handelSearch()
        collectData()
    }

    private fun setListeners() {
        binding.apply {
            constraintLayout2.setOnClickListener(this@SearchActivity)
            recyclerSearchActivity.setOnClickListener(this@SearchActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        requesters()
    }

    private fun requesters() {
        binding.apply {
            etSearch.requestFocus()
            etSearch.postDelayed({
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
            },250)
        }
    }


    private fun setRecycler(){
        adapter = AdsAdapter(this)
        adapter.setList(user , list , this)
        binding.apply {
           recyclerSearchActivity.adapter = adapter
           recyclerSearchActivity.layoutManager =  LinearLayoutManager(this@SearchActivity)
        }

    }


    private fun handelSearch(){
        var job : Job? = null
        binding.etSearch.addTextChangedListener{
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_DELAY)
                binding.apply {
                    etSearch.text?.let {
                        if(etSearch.text.toString().isNotEmpty()){
                            searchViewModel.search(user,etSearch.text.toString())
                        }
                    }
                }
            }
        }
    }

    override fun getItemPosition(position: Int) {
        if (debouncing) {
            // Remove the previous runnable
            debounceRunnable?.let { debounceHandler.removeCallbacks(it) }
        } else {
            // This is the first click, so open the item
            debouncing = true
            callAdvertiserFragment(position)
        }
        // Start a new timer
        debounceRunnable = Runnable { debouncing = false }
        debounceHandler.postDelayed(debounceRunnable!!,DEBOUNCE_DELAY_MILLIS)

    }

    override fun getFavItemPosition(position: Int, favImage: ImageView) {
        val adId: String = list[position].id
        val favPos: Int = adIsInFavList(adId)
        if (favPos != -1) {
            user.favAds.removeAt(favPos)
            favViewModel.deleteFavAd(user.uId, list[position])
            favImage.setImageResource(R.drawable.fav_uncheck_icon)
        } else {
            if (user.favAds == null) user.initFavAdsList()
            favViewModel.addFavAd(user.uId, list[position])
            user.favAds.add(list[position])
            favImage.setImageResource(R.drawable.fav_checked_icon)
        }
    }

    private fun callAdvertiserFragment(pos: Int) {
        if (!advertiserFragment.isAdded) {
            val bundle = Bundle()
            bundle.putParcelable(getString(R.string.userVal), user)
            bundle.putParcelable(getString(R.string.adVal), list[pos])
            advertiserFragment.arguments = bundle
            advertiserFragment.show(supportFragmentManager, advertiserFragment.tag)
        }
    }

    private fun adIsInFavList(id: String): Int {
        for (i in user.favAds.indices) {
            if (user.favAds[i].id == id) {
                return i
            }
        }
        return -1
    }

    private fun collectData() = lifecycleScope.launch {
        searchViewModel.searchState.collect{
           if(it.isNotEmpty()){
               binding.recyclerSearchActivity.visibility = View.VISIBLE
               list = it as ArrayList<Ad>
               adapter.updateList(list)
           }else {
               binding.recyclerSearchActivity.visibility = View.GONE
           }
        }
    }

    override fun onClick(v: View?) {
        binding.apply {
            when(v?.id ){
                constraintLayout2.id , recyclerSearchActivity.id -> {
                    hideKeyboard(v)
                }
            }
        }

    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}