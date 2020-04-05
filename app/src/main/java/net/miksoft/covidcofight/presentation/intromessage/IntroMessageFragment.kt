package net.miksoft.covidcofight.presentation.intromessage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.fragment_intro_message.*
import net.miksoft.covidcofight.R
import net.miksoft.covidcofight.presentation.intromessage.IntroMessageFragmentDirections

class IntroMessageFragment : Fragment() {

    private lateinit var pageAdapter: PageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pageAdapter =
            PageAdapter(
                this
            )
        pagerView.adapter = pageAdapter
    }

    fun nextPage() = pagerView.setCurrentItem(1, true)
}

class PageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(index: Int): Fragment {
        val fragment =
            PageFragment()
        fragment.arguments = Bundle().apply {
            putInt(PAGE_INDEX, index)
        }
        return fragment
    }
}

private const val PAGE_INDEX = "page_index"

class PageFragment : Fragment() {

    private val pageIndex by lazy {
        arguments?.getInt(PAGE_INDEX) ?: throw IllegalArgumentException("Page index not provided")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            when (pageIndex) {
                0 -> R.layout.intro_message_0
                1 -> R.layout.intro_message_1
                else -> throw IllegalArgumentException("Unknown index")
            }, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.nextButtonView).setOnClickListener {
            when (pageIndex) {
                0 -> (parentFragment as IntroMessageFragment).nextPage()
                1 -> view.findNavController()
                    .navigate(IntroMessageFragmentDirections.actionIntroMessageFragmentToSignUpFragment())
            }
        }
    }
}
