package my.app.bankguaranteemonitor;

public class Forward {
    public String From,Image,Notify,Remarks,To,Date;
    public  int ID;
    Forward(){}
    public Forward(String name,String img,String rem,String to,String dat,String notify){
        From = name;
        Image = img;
        Notify = notify;
        Remarks = rem;
        Date = dat;
        To = to;
    }
    public  Forward(int id,String name,String to,String rem,String d){
        From = name;
        To = to;
        Remarks = rem;
        Notify = "0";
        Date = d;
        ID = id;
    }
}
