package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.testapp.NewNotificationListAdapter
import com.google.android.material.snackbar.Snackbar
import com.rnsoft.colabademo.databinding.FragmentNotificationsBinding
import com.rnsoft.colabademo.utils.RecyclerTouchListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class NotificationsFragment : BaseFragment(), NotificationClickListener,RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var coordinatorLayout: CoordinatorLayout
    private var _binding: FragmentNotificationsBinding? = null
    private val dashBoardViewModel: DashBoardViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var notificationRecycleView: RecyclerView
    private var notificationArrayList: ArrayList<NotificationItem> = ArrayList()
    private var readArrayList: ArrayList<Int> = ArrayList()
    private var deleteArrayList: ArrayList<Int> = ArrayList()
    private var newNotificationAdapter: NewNotificationListAdapter = NewNotificationListAdapter(notificationArrayList, this@NotificationsFragment)
    private var pageSize = 20
    private var mediumId = 1
    private var lastNotificationId = -1
    private var touchListener: RecyclerTouchListener? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        coordinatorLayout = root.findViewById(R.id.coordinator_layout)
        notificationRecycleView = root.findViewById(R.id.notification_recycle_view)
        val linearLayoutManager = LinearLayoutManager(activity)
        notificationRecycleView.apply {
            this.layoutManager = linearLayoutManager
            this.setHasFixedSize(true)
            newNotificationAdapter =
                NewNotificationListAdapter(notificationArrayList, this@NotificationsFragment)
            this.adapter = newNotificationAdapter
        }

        binding.newNotificationButton.transformationMethod = null
        binding.newNotificationButton.setOnClickListener {
            notificationRecycleView.smoothScrollToPosition(0)
        }
        lifecycleScope.launchWhenResumed {
            sharedPreferences.getString(AppConstant.token, "")?.let {
                dashBoardViewModel.getNotificationListing(
                    token = it,
                    pageSize = pageSize, lastId = -1, mediumId = mediumId
                )
            }
        }
        dashBoardViewModel.notificationItemList.observe(viewLifecycleOwner, {
            if (it.size > 0) {
                //Log.e("it-", it.size.toString() + "  " + it)
                notificationArrayList.addAll(it)
                newNotificationAdapter.notifyDataSetChanged()
                val seenIds: ArrayList<Int> = ArrayList()
                for (i in it) {
                    i.id?.let { seenId ->
                        //Log.e("seen-id-" ,seenId.toString())
                        seenIds.add(seenId)
                    }
                }

                it[it.size - 1].id?.let { lastId ->
                    lastNotificationId = lastId
                }
                // load the count notification service and other service and update count by LiveData...
                sharedPreferences.getString(AppConstant.token, "")?.let { token ->
                    dashBoardViewModel.getNotificationCountT(token)
                    dashBoardViewModel.seenNotifications(token, seenIds)
                }
            }

        })

        val scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadFurtherNotifications()
            }
        }

        notificationRecycleView.addOnScrollListener(scrollListener)

        // swipe
        touchListener = RecyclerTouchListener(requireActivity(), notificationRecycleView)
        touchListener!!
            .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                override fun onRowClicked(position: Int) {}
                override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
            })
            .setSwipeOptionViews(R.id.delete_task)
            .setSwipeable(R.id.rowFG, R.id.rowBG, object :
                RecyclerTouchListener.OnSwipeOptionsClickListener {

                override fun onSwipeOptionClicked(viewID: Int, position: Int) {

                    // backup of removed item for undo purpose
                    val deletedItem = notificationArrayList[position]
                    val deletedIndex = position

                    val deleteId = deletedItem.id
                    deleteId?.let {
                        if (!deleteArrayList.contains(it))
                            deleteArrayList.add(it)
                    }
                    // remove the item from recycler view
                    newNotificationAdapter.removeItem(position)

                    val snackbar = Snackbar.make(
                            coordinatorLayout,
                            "The notification has been removed.",
                            Snackbar.LENGTH_LONG
                        )

                    snackbar.setBackgroundTint(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colaba_apptheme_blue
                        )
                    )
                    snackbar.setActionTextColor(resources.getColor(R.color.white, activity?.theme))
                    snackbar.setTextColor(resources.getColor(R.color.white, activity?.theme))
                    snackbar.setAction("UNDO") { // undo is selected, restore the deleted item
                        newNotificationAdapter.restoreItem(deletedItem, deletedIndex)
                        val deleteId = deletedItem.id
                        deleteId?.let {
                            if (deleteArrayList.contains(it))
                                deleteArrayList.remove(it)
                        }
                    }
                    snackbar.show()
                    //Log.e("position", "Index: " + deletedIndex + " position" + deletedItem)

                }
            })
        notificationRecycleView.addOnItemTouchListener(touchListener!!)
        super.addListeners(binding.root)
        return root
    }


    private fun loadFurtherNotifications() {
        sharedPreferences.getString(AppConstant.token, "")?.let {
            dashBoardViewModel.getFurtherNotificationList(
                token = it, pageSize = pageSize, lastId = lastNotificationId, mediumId = mediumId
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(view: View) {}

    override fun onNotificationRead(position: Int) {
        val readId = notificationArrayList[position].id
        readId?.let {
            if (!readArrayList.contains(it))
                readArrayList.add(it)
        }
    }

    override fun onNotificationDelete(position: Int) {
        val deleteId = notificationArrayList[position].id
        deleteId?.let {
            if (!deleteArrayList.contains(it))
                deleteArrayList.add(it)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        //Log.e("readArrayList-", readArrayList.size.toString())
        //Log.e("deleteArrayList-", deleteArrayList.size.toString())
        sharedPreferences.getString(AppConstant.token, "")?.let {
            if (readArrayList.size > 0)
                dashBoardViewModel.readNotifications(it, readArrayList)
        }
        sharedPreferences.getString(AppConstant.token, "")?.let {
            if (deleteArrayList.size > 0)
                dashBoardViewModel.deleteNotifications(it, deleteArrayList)
        }
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorReceived(event: WebServiceErrorEvent) {
        if (event.isInternetError)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        else
            if (event.errorResult != null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        TODO("Not yet implemented")
    }

}








    //Irfan work

/*
// irfan's code
 val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
    RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notificationRecycleView)
*/

/* override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
     if (viewHolder is NewNotificationListAdapter.ContentViewHolder) {
         // get the removed item name to display it in snack bar
         val name = notificationArrayList[viewHolder.getAdapterPosition()].id

         // backup of removed item for undo purpose
         val deletedItem = notificationArrayList[viewHolder.getAdapterPosition()]
         val deletedIndex = viewHolder.getAdapterPosition()

         val deleteId = deletedItem.id
         deleteId?.let {
             if (!deleteArrayList.contains(it))
                 deleteArrayList.add(it)
         }

         // remove the item from recycler view
         newNotificationAdapter.removeItem(viewHolder.getAdapterPosition())

         // showing snack bar with Undo option
         val snackbar = Snackbar
             .make(coordinatorLayout, "The notification has been removed.", Snackbar.LENGTH_LONG)

         snackbar.setBackgroundTint(ContextCompat.getColor(requireActivity(), R.color.colaba_apptheme_blue))
         snackbar.setActionTextColor(resources.getColor(R.color.white, activity?.theme))
         snackbar.setTextColor(resources.getColor(R.color.white, activity?.theme))
         snackbar.setAction("UNDO") { // undo is selected, restore the deleted item
             newNotificationAdapter.restoreItem(deletedItem, deletedIndex)
             val deleteId = deletedItem.id
             deleteId?.let {
                 if (deleteArrayList.contains(it))
                     deleteArrayList.remove(it)
             }
         }
         snackbar.show()
     }
 }

 */

