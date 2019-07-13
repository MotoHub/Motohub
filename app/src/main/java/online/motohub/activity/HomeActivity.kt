package online.motohub.activity

import android.os.Bundle
import android.view.View
import online.motohub.R


class HomeActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun onClick(v: View?) {
//        when (v!!.id) {
//            R.id.newsFeedView -> {
//                nextScreen(MainHomeActivity::class.java)
//            }
//            R.id.onDemandView -> {
//
//            }
//            R.id.eventVideosView -> {
//
//            }
//            R.id.findEventView -> {
//
//            }
//            R.id.myProfileView -> {
//
//            }
//        }
        val bundle = Bundle()
        bundle.putInt("ID", v!!.id)
        nextScreen(MainHomeActivity::class.java, bundle)
    }
    /**
     *  For Animation
     *  Commented for future pupose
     */
//    var show = false
//    private val constraintSet1 = ConstraintSet()
//    private val constraintSet2 = ConstraintSet()
//    private val transition = ChangeBounds()
//
//    private fun setAnimateLay() {
//        constraintSet1.clone(parentLay)
//        constraintSet2.clone(activity, R.layout.activity_home_view)
//
//        transition.interpolator = AnticipateInterpolator(1.0f)
//        transition.duration = 1000
//
//        val han = Handler()
//        han.postDelayed({
//            showAnimation()
//        }, 100)
//    }
//
//    private fun clickAction() {
//        if (show)
//            revertAnimation()
//        else
//            showAnimation()
//    }
//
//    private fun showAnimation() {
//        show = true
//        TransitionManager.beginDelayedTransition(parentLay, transition)
//        constraintSet1.applyTo(parentLay)
//        TransitionManager.beginDelayedTransition(parentLay, transition)
//        constraintSet2.applyTo(parentLay)
//    }
//
//    private fun revertAnimation() {
//        show = false
//        TransitionManager.beginDelayedTransition(parentLay, transition)
//        constraintSet2.applyTo(parentLay)
//        TransitionManager.beginDelayedTransition(parentLay, transition)
//        constraintSet1.applyTo(parentLay)
//
//    }
}

