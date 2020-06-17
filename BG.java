package my.app.bankguaranteemonitor;

public class BG {
    public long amount;
    public String bgNigam,bgDivision,bgType;
    public String date_init,remarks,name_of_work;
    public String  bgNum,date_start,date_expire;

    BG() {}

    BG(long amt, String ngm, String div, String init,String typ,String rem,String name) {
        amount = amt;
        bgNigam = ngm;
        bgDivision = div;
        bgType = typ;
        date_init = init;
        remarks = rem;
        name_of_work = name;
    }

    BG(String num,String ngm,String div,String typ,long amt,String stDate,String ExpDate,String work) {
        name_of_work = work;
        bgNum = num;
        bgNigam = ngm;
        bgDivision = div;
        bgType = typ;
        amount = amt;
        date_start = stDate;
        date_expire = ExpDate;
    }
}