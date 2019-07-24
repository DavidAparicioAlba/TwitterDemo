package com.example.twitterdemo

class Ticket {
    var tweetId:String?=null
    var tweetText:String?=null
    var tweetImageURL:String?=null
    var personId:String?=null
    constructor(tweetId:String, tweetText:String, tweetImage:String, personId:String){
        this.tweetId=tweetId
        this.tweetText=tweetText
        this.tweetImageURL=tweetImage
        this.personId=personId
    }
}