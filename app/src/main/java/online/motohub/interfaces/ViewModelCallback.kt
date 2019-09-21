package online.motohub.interfaces

import online.motohub.newdesign.bl.ViewModelAlert

interface ViewModelCallback {

    abstract fun showProgress()

    abstract fun hideProgress()

    abstract fun showMessage(errorMessage: String)

    abstract fun showAlert(viewModelAlert: ViewModelAlert)

    // Used to handle parallel progress across multiple view model
    abstract fun getProgressCount(): Int

    abstract fun setProgressCount(progressCount: Int)
}