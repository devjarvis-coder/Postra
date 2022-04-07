package com.xcodelabs.postra.Model

class Post {

    private var postid: String = ""
    private var postimage: String = ""
    private var postvideo : String = ""
    private var publisher: String = ""
    private var description: String = ""


    constructor()


    constructor(postid: String, postvideo: String, postimage: String, publisher: String, description: String) {
        this.postid = postid
        this.postimage = postimage
        this.postvideo = postvideo
        this.publisher = publisher
        this.description = description
    }


    fun getPostid(): String{
        return postid
    }

    fun getPostimage(): String{
        return postimage
    }

    fun getPostvideo(): String{
        return postvideo
    }

    fun getPublisher(): String{
        return publisher
    }

    fun getDescription(): String{
        return description
    }

    //set

    fun setPostid(postid: String)
    {
        this.postid = postid
    }

    fun setPostimage(postimage: String)
    {
        this.postimage = postimage
    }

    fun setPostvideo(postvideo: String)
    {
        this.postvideo = postvideo
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun setDescription(description: String)
    {
        this.description = description
    }
}