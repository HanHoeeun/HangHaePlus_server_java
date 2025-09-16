package kr.hhplus.be.server.layered.queue;

public class QueueKeys {
    static String witingZset(){
        return "queue : waiting";
    }
    static String userToken(String userId){
        return "user : queue : token :" + userId;
    }
    static String tokenHash(String token){
        return "qt :" + token;
    }
    static String activeKey(String token){
        return "queue : active : " + token;
    }
    private QueueKeys(){

    }
}
