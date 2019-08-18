package online.motohub.model

class ApiInputModel {

    var userID: Int = 0
    var fields: String = ""
    var filter: String = ""
    var related: String = ""
    var order: String = ""
    var limit: Int = 10
    var offset: Int = 0
    var includeCount: Boolean = false



}