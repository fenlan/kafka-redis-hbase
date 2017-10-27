package redis;

import com.google.gson.Gson;
import data.Record;
import hbase.HBaseInsert;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedisToHbase {

    private static String  redisHost = "192.168.1.104";
    private static int  redisPort = 6379;

    private static Jedis jedis = new Jedis(redisHost,redisPort);
    private static HBaseInsert in = new HBaseInsert();
    private static int i = 0;
    private static int j = 0;
    private static List list = new ArrayList();
    private static String recordFilePath = "./data/record.json";
    public static void main(String[] args){

        try {
            BufferedReader br = new BufferedReader(new FileReader(recordFilePath));
            while(br.readLine()!=null) {
                j++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
//                System.out.println(String.format("receive redis published message %s, channel %s, message %s", i++, channel, message));
//                List list = new ArrayList();
//                Record record;
//                Gson gson = new Gson();
//                record = gson.fromJson(message, Record.class);
//                list.add(record);
//                in.insertRecordsToHBase(list);

                // insert 1000 messages once a time
                System.out.println(String.format("receive redis published message %s, channel %s, message %s", i++, channel, message));
                Record record;
                Gson gson = new Gson();
                record = gson.fromJson(message, Record.class);
                list.add(record);
                if (i % 1000 == 0 || i == j ) {
                    in.insertRecordsToHBase(list);
                    list.clear();
                }
            }
        };
        jedis.subscribe(jedisPubSub, "test");
    }
}
