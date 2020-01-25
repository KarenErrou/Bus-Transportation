package com.example.lbt.lbt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class DijkstraAlgorithmTest extends AppCompatActivity {

    public static int count = 0;

    private static DijkstraAlgorithm da;
    private static Graph graph;
    private static List<Edge> model;

    static int source;
    static int destination;
    static String time;
    static String category;

    static String direct;
    ArrayList<List> Scehdules = new ArrayList<>();
    ArrayList<String> listS = null;

    private ListView lvSchedules;
    private SchedulesListAdapter adapter;
    private List<Schedules> mSchedulesList;

    Boolean insert = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);

        Bundle info = this.getIntent().getExtras();
        source = info.getInt("idSource");
        destination = info.getInt("idDestination");
        time = info.getString ("timeTrip");
        category = info.getString("category");
        direct =info.getString("direct");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btn = (Button) findViewById(R.id.accept);

        try {
            setUp(source,destination,time,category);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert = true;
                try {
                    buildModel();
                } catch (PathNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void setUp (int source, int destination, String time,String category) throws Exception {

        this.source =  source;
        this.destination = destination;
        this.time = time;
        this.category = category;

        buildModel();

    }

    private void buildModel() throws PathNotFoundException {

        BackTask db = new BackTask();
        db.execute();
    }

    private class BackTask extends AsyncTask<Void, Void, Void> {

        ArrayList<String> list;
        Connection conn = null;
        Statement stmt = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        Statement stmt4 = null;

        Statement stmt5 = null;
        Statement stmt6 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;

        String nameFrom = null;
        String nameTo = null;

        ResultSet rs4 =null;

        ResultSet rs5 =null;
        ResultSet rs6 =null;

        Boolean directNotFound = false;
        Boolean notfoundAttime = false;
        protected Void doInBackground(Void...params){

            if(count == 0) {
                if (insert == false) {
                    ArrayList<Integer> neighbords = new ArrayList<>();
                    ArrayList<Integer> done = new ArrayList<>();
                    model = new ArrayList<Edge>();
                    neighbords.add(source);//1 is equivalent to the source id
                    try {
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                        conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                        while (!neighbords.isEmpty()) {
                            int src = neighbords.get(0);
                            String sql = "SELECT * FROM neighbourhood WHERE source = '" + src + "'";
                            stmt = conn.createStatement();
                            rs = stmt.executeQuery(sql);
                            while (rs.next()) {
                                neighbords.add(rs.getInt("destination"));
                                model.add(new Edge(new Vertex<>(src), new Vertex<>(rs.getInt("destination")), rs.getInt("weight")));
                            }
                            done.add(src);
                            neighbords.remove(0);
                        }
                        graph = new Graph(model);
                        da = new DijkstraAlgorithm(graph);
                        da.execute(new Vertex<>(source));

                        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

                        String fromHour = null;
                        String toHour = null;
                        int fromStation = 0;
                        int toStation = 0;
                        String bus = null;

                        int price = 0;
                        int priceFromCat = 0;

                        int indexOf = 0;
                        String newTime = time;
                        int sizeOfSchedules = da.getPath(new Vertex<>(destination)).size();

                        //get from database the price of the category
                        Statement stmtCat = null;
                        ResultSet resCat = null;
                        String sqlCat = "select price from category where name ='" + category + "'";
                        stmtCat = conn.createStatement();
                        resCat = stmtCat.executeQuery(sqlCat);
                        if (resCat.next()) {
                            priceFromCat = resCat.getInt("price");
                        }

                        //if the user choose direct
                        if (direct.equals("yes")) {
                            System.out.println("-------ssssss---------" + direct);

                            Vertex src = da.getPath(new Vertex<>(destination)).get(0);
                            Vertex dest = da.getPath(new Vertex<>(destination)).get(da.getPath(new Vertex<>(destination)).size() - 1);
                            System.out.println("-------time---------" + time);
                            String sqlDirect = "SELECT * FROM schedule where fstation = " + src + " and fromhour = '" + time + "' and tostation = '" + dest + "'";
                            System.out.println("-------ssssss---------" + sqlDirect);
                            Statement stmtDirect = conn.createStatement();
                            ResultSet resDirect = stmtDirect.executeQuery(sqlDirect);
                            listS = new ArrayList<>();
                            if (resDirect.next()) {
                                price = resDirect.getInt("price") + priceFromCat;
                                fromStation = resDirect.getInt("fstation");
                                fromHour = resDirect.getString("fromhour");
                                toStation = resDirect.getInt("tostation");
                                toHour = resDirect.getString("tohour");
                                bus = resDirect.getString("busid");

                                listS.add(String.valueOf(price));
                                listS.add(String.valueOf(fromStation));
                                listS.add(fromHour);
                                listS.add(toHour);
                                listS.add(String.valueOf(toStation));
                                listS.add(bus);

                                Scehdules.add(listS);
                            } else {
                                directNotFound = true;
                            }
                        }
                        //if the user did not choose direct
                    else{
                            try{
                            boolean don = false;
                                int count =0;
                            System.out.println("OPPPPPPPPP");
                            LinkedList<Vertex> array1 =  da.getPath(new Vertex<>(destination));
                            do {

                                for (int i = array1.size() - 1; i >= 1 ; i--) {
                                    Vertex source = array1.get(0);
                                    Vertex dest = array1.get(i);
                                    String sql1 = "SELECT * FROM schedule where fstation = " + source + " and fromhour = '" + newTime + "'and tostation = " + dest;
                                    ;
                                    stmt1 = conn.createStatement();
                                    rs1 = stmt1.executeQuery(sql1);
                                    System.out.println("OPPPPPPPPPPPPP " + sql1);
                                    if (rs1.next()) {
                                        System.out.println("OPPPPPPPP1");
                                        listS = new ArrayList<>();
                                        if (dest.toString().equalsIgnoreCase(String.valueOf(destination))) {

                                            price = rs1.getInt("price") + priceFromCat;
                                            fromStation = rs1.getInt("fstation");
                                            fromHour = rs1.getString("fromhour");
                                            toStation = rs1.getInt("tostation");
                                            toHour = rs1.getString("tohour");
                                            bus = rs1.getString("busid");

                                            listS.add(String.valueOf(price));
                                            listS.add(String.valueOf(fromStation));
                                            listS.add(fromHour);
                                            listS.add(toHour);
                                            listS.add(String.valueOf(toStation));
                                            listS.add(bus);
                                            Scehdules.add(listS);
                                            don = false;


                                        } else {
                                            System.out.println("OPPPPPPPP2");
                                            for (int j = 0; j < i; j++) {
                                                price = rs1.getInt("price") + priceFromCat;
                                                fromStation = rs1.getInt("fstation");
                                                fromHour = rs1.getString("fromhour");
                                                toStation = rs1.getInt("tostation");
                                                toHour = rs1.getString("tohour");
                                                bus = rs1.getString("busid");

                                                listS.add(String.valueOf(price));
                                                listS.add(String.valueOf(fromStation));
                                                listS.add(fromHour);
                                                listS.add(toHour);
                                                listS.add(String.valueOf(toStation));
                                                listS.add(bus);

                                                Scehdules.add(listS);
                                                array1.remove(0);
                                                don= true;

                                                count =0;
                                            }
                                        }
                                    }
                                }
                                if(count > 0) {
                                    Date toHourTime = (Date) dateFormat.parse(newTime);

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(toHourTime);
                                    cal.add(Calendar.MINUTE, 5);
                                    newTime = dateFormat.format(cal.getTime());
                                }
                                count++;
                            }while(don);
                    }catch (Exception e){

                            }}


//                        else {
//                            boolean found0 = false;
//                            boolean found = false;
//                            //20-22-25 check if 20->25 then 20->22 at time 6 if found check 22->25 at 7 (to hour) if not fount add
//                            for (int i = 0; i < sizeOfSchedules; i++) {
//
//                                ListIterator<Vertex> listIter = da.getPath(new Vertex<>(destination)).listIterator(da.getPath(new Vertex<>(destination)).size());
//
//
//                                Vertex fstat = null;
//                                fstat = da.getPath(new Vertex<>(destination)).get(i);
//                                //Last element
//                                Vertex tdest = da.getPath(new Vertex<>(destination)).get(da.getPath(new Vertex<>(destination)).size() - 1);
//                                String sql1 = "SELECT * FROM schedule where fstation = " + fstat + " and fromhour = '" + newTime + "' ";
//                                while (listIter.hasPrevious()) {
//                                    if (fromStation == source && toStation == destination) {
//                                        break;
//                                    }
//                                    listS = new ArrayList<>();
//                                    Vertex prev = listIter.previous();
//                                    int index = listIter.previousIndex();
//                                    if (prev.equals(fstat)) {
//                                        break;
//                                    }
//                                    if (fstat.equals(tdest)) {
//                                        break;
//                                    } else {
//                                        String sql2 = sql1 + "and tostation = " + prev;
//                                        System.out.println("OOOOO8 " + sql2);
//
//                                        System.out.println(sql2);
//
//                                        stmt1 = conn.createStatement();
//                                        rs1 = stmt1.executeQuery(sql2);
//                                        if (rs1.next()) {
//                                            price = rs1.getInt("price") + priceFromCat;
//                                            fromStation = rs1.getInt("fstation");
//                                            fromHour = rs1.getString("fromhour");
//                                            toStation = rs1.getInt("tostation");
//                                            toHour = rs1.getString("tohour");
//                                            bus = rs1.getString("busid");
//
//                                            listS.add(String.valueOf(price));
//                                            listS.add(String.valueOf(fromStation));
//                                            listS.add(fromHour);
//                                            listS.add(toHour);
//                                            listS.add(String.valueOf(toStation));
//                                            listS.add(bus);
//
//                                            Scehdules.add(listS);
//
//                                        }
//
//                                    }
//                                    newTime = toHour;
//                                    indexOf = index;
//                                }
//                            }
//
//                            if (toStation != destination && toStation != 0 && toHour != null) {
//                                ArrayList<Vertex> newArray = new ArrayList<>();
//
//                                for (int j = indexOf; j < sizeOfSchedules; j++) {
//                                    newArray.add(da.getPath(new Vertex<>(destination)).get(j));
//
//                                }
//                                Date toHourTime = (Date) dateFormat.parse(toHour);
//
//                                Calendar cal = Calendar.getInstance();
//                                cal.setTime(toHourTime);
//
//                                Calendar calMax = Calendar.getInstance();
//                                calMax.setTime(toHourTime);
//                                calMax.add(Calendar.MINUTE, 120);
//                                String add = dateFormat.format(cal.getTime());
//                                String MaxTime = dateFormat.format(calMax.getTime());
//
//                                for (int j = 0; j < newArray.size() && !found; j++) {
//                                    while (!add.equals(MaxTime)) {
                        Date toHourTime = (Date) dateFormat.parse(toHour);
//                                        cal.add(Calendar.MINUTE, 5);
//                                        add = dateFormat.format(cal.getTime());
//                                        System.out.println("------------addaddaddadd-----" + add);
//                                        Vertex fstat = newArray.get(j);
//                                        String sql4 = "SELECT * FROM schedule where fstation = " + fstat + " and fromhour = '" + add + "'";
//                                        ListIterator<Vertex> listIter2 = newArray.listIterator(newArray.size());
//                                        while (listIter2.hasPrevious() && !found) {
//
//                                            Vertex prev = listIter2.previous();
//                                            if (prev.equals(fstat)) {
//                                                break;
//                                            } else {
//                                                sql4 = sql4 + "and tostation = '" + prev + "'";
//                                                System.out.println("OOOOOOO8" + sql4);
//                                                stmt4 = conn.createStatement();
//                                                rs4 = stmt4.executeQuery(sql4);
//                                                if (rs4.next()) {
//                                                    listS = new ArrayList<>();
//                                                    price = rs4.getInt("price") + priceFromCat;
//                                                    fromStation = rs4.getInt("fstation");
//                                                    fromHour = rs4.getString("fromhour");
//                                                    toStation = rs4.getInt("tostation");
//                                                    toHour = rs4.getString("tohour");
//                                                    bus = rs4.getString("busid");
//
//                                                    listS.add(String.valueOf(price));
//                                                    listS.add(String.valueOf(fromStation));
//                                                    listS.add(fromHour);
//                                                    listS.add(toHour);
//                                                    listS.add(String.valueOf(toStation));
//                                                    listS.add(bus);
//
//                                                    Scehdules.add(listS);
//                                                    System.out.println("OOOOOOO9 " +  listS);
//                                                    System.out.println("OOOOOOO10 " +  prev);
//                                                    if (prev.toString().equalsIgnoreCase(String.valueOf(destination))) {
//                                                        System.out.println("OOOOOO11");
//                                                        found = true;
//                                                    }
//                                                } else {
//                                                    notfoundAttime = true;
//                                                    System.out.println("OOOOOO12");
//                                                }
//                                                System.out.println("OOOOOO13");
//                                            }
//                                                System.out.println("OOOOOO14");
//
//                                        }
//                                            System.out.println("OOOOOO15");
//                                        System.out.println("OOOOOO16");
//                                }
//                                    System.out.println("OOOOOO17");
//                            }
//                                System.out.println("OOOOOO18");}
//
//
//                            System.out.println("-------NOT DIRECT---------");
//                        }
                        System.out.println("OOOOOO19");
                        System.out.println("-------ssssss---------");
                        System.out.println("-------ssssss---------" + Scehdules.toString());


                        //////// get Names of stations
                        if (!Scehdules.isEmpty()) {
                            for (int i = 0; i < Scehdules.size(); i++) {
                                List<String> list = Scehdules.get(i);

                                String fromRes = list.get(1);
                                String toRes = list.get(4);

                                String sql5 = "select name from station where station_id ='" + fromRes + "'";
                                String sql6 = "select name from station where station_id ='" + toRes + "'";
                                stmt5 = conn.createStatement();
                                rs5 = stmt5.executeQuery(sql5);
                                if (rs5.next()) {
                                    nameFrom = rs5.getString("name");
                                    list.add(nameFrom);
                                }

                                stmt6 = conn.createStatement();
                                rs6 = stmt6.executeQuery(sql6);
                                if (rs6.next()) {
                                    nameTo = rs6.getString("name");
                                    list.add(nameTo);
                                }

                            }

                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        System.out.println("LOGINDB EXCEPTION " + e);
                        e.printStackTrace();
                    }
                    count = 1;
                }
            }else{
                Calendar calendar = Calendar.getInstance();
                java.sql.Date sysdate = new java.sql.Date(calendar.getTime().getTime());
                System.out.println("-ourJavaDateObject--"+sysdate);

                for(int i=0;i<Scehdules.size();i++){
                    List<String> list = Scehdules.get(i);
                    System.out.println("-insertinsertinsert--"+list);
                    String priceRes = list.get(0);
                    String fromRes = list.get(1);
                    String fromHRes = list.get(2);
                    String toHRes = list.get(3);
                    String toRes = list.get(4);
                    String busRes = list.get(5);

                    try {
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                        conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/lbt", "root", "");
                        Statement statement = conn.createStatement();
                        Intent intent = new Intent(DijkstraAlgorithmTest.this, PaymentActivity.class);
                        intent.putExtra("type", "trip");
                        intent.putExtra("schedules", Scehdules);
                        intent.putExtra("trip","Single Trip");
                        intent.putExtra("date",sysdate);
                        startActivity(intent);
                        //String insert = "insert into ticket (client,fstation,tostation,category,Trip,TotalPrice,date) " +
                         //       "select 1,'"+fromRes +"','"+ toRes+ "',c.category_id,t.trip_id,'"+priceRes+"','" + sysdate +"'"+
                         //       "                        from category c,trip t where c.name = '"+ category +"' and t.name = 'Single Trip'";
                        //System.out.println("-insertinsertinsert--"+insert);
                        //statement.executeUpdate(insert);
                        //Toast.makeText(getApplicationContext(), "Ticket bought "  , Toast.LENGTH_SHORT).show();

                    }catch(Exception e){
                        System.out.println("LOGINDB EXCEPTION " + e);
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        protected void onPostExecute(Void avoid) {
            if(directNotFound == true){
                setResult(RESULT_OK);
                finish();
            }
            if(Scehdules.isEmpty()){
                setResult(RESULT_OK);
                finish();
            }else if(!Scehdules.isEmpty()){
                LinearLayout L2 = new LinearLayout(DijkstraAlgorithmTest.this);
                L2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                L2.setGravity(Gravity.CENTER);


                lvSchedules = (ListView) findViewById(R.id.listview_schedules);
                mSchedulesList = new ArrayList<>();
                for (int i = 0; i < Scehdules.size(); i++) {

                    List<String> list = Scehdules.get(i);

                    String priceRes = list.get(0);
                    String fromRes = list.get(6);
                    String fromHRes = list.get(2);
                    String toHRes = list.get(3);
                    String toRes = list.get(7);
                    String busRes = list.get(5);


                    mSchedulesList.add(new Schedules(priceRes, fromHRes, toHRes, differenceTime(fromHRes, toHRes), fromRes, toRes, busRes));
                    System.out.println("---------mSchedulesListmSchedulesList---------------" + mSchedulesList);
                    adapter = new SchedulesListAdapter(getApplicationContext(), mSchedulesList);


                }

                lvSchedules.setAdapter(adapter);

//                lvSchedules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Toast.makeText(getApplicationContext(), "Clicked on : " + view.getTag(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        }


    }


    public String differenceTime (String from , String to){
        long difference = 0;
        String res = null;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date date1 = format.parse(from);
            Date date2 = format.parse(to);
            difference = date2.getTime() - date1.getTime();
            DecimalFormat Formatter = new DecimalFormat("###,###");

            int diffhours = (int) (difference / (60 * 60 * 1000));

            int diffmin = (int) (difference / (60 * 1000));

            if(diffhours == 1 && diffmin == 60){
                diffmin = 0 ;
            }
            res = Formatter.format(diffhours) +"H " + Formatter.format(diffmin) +" Min";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }



}