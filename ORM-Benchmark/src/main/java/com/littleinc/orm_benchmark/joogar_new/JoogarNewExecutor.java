package com.littleinc.orm_benchmark.joogar_new;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.littleinc.orm_benchmark.BenchmarkExecutable;

import net.skoumal.joogar2.JoogarRecord;
import net.skoumal.joogar2.Transaction;
import net.skoumal.joogar2.shared.Joogar;
import net.skoumal.joogar2.shared.JoogarDatabase;

import java.util.LinkedList;
import java.util.List;

import static com.littleinc.orm_benchmark.util.Util.getRandomString;

/**
 * Created by Jan Bína on 04/17/2017
 */
public class JoogarNewExecutor implements BenchmarkExecutable
{

    private static final String TAG = "JoogarNewExecutor";
    private Context applicationContext;

    @Override
    public void init(Context context, boolean useInMemoryDb)
    {
        Log.d(TAG, "Creating DataBaseHelper");
        applicationContext = context.getApplicationContext();
    }

    @Override
    public long createDbStructure() throws SQLException
    {
        long start = System.nanoTime();

        Joogar.init(applicationContext);

        return System.nanoTime() - start;
    }

    @Override
    public long writeWholeData() throws SQLException
    {
        final List<User> users = new LinkedList<User>();
        for(int i = 0; i < NUM_USER_INSERTS; i++)
        {
            User newUser = new User();
            newUser.lastName = (getRandomString(10));
            newUser.firstName = (getRandomString(10));

            users.add(newUser);
        }

        final List<Message> messages = new LinkedList<>();
        for (int i = 0; i < NUM_MESSAGE_INSERTS; i++) {
            Message newMessage = new Message();
            newMessage.commandId = i;
            newMessage.sortedBy = System.nanoTime();
            newMessage.content = getRandomString(100);
            newMessage.clientId = System.currentTimeMillis();
            newMessage
                    .senderId = (Math.round(Math.random() * NUM_USER_INSERTS));
            newMessage
                    .channelId = (Math.round(Math.random() * NUM_USER_INSERTS));
            newMessage.createdAt = ((int) (System.currentTimeMillis() / 1000L));

            messages.add(newMessage);
        }

        long start = System.nanoTime();

        UserTable.getDatabase().executeTransaction(new Transaction() {
            @Override
            public void execute() {
                for (User u : users) {
                    JoogarRecord.insert(u);
                }
                Log.d(TAG, "Done, wrote " + NUM_USER_INSERTS + " users");
                for (Message m : messages) {
                    JoogarRecord.insert(m);
                }
                Log.d(TAG, "Done, wrote " + NUM_MESSAGE_INSERTS + " messages");
            }
        });

        return System.nanoTime() - start;
    }

    @Override
    public long readWholeData() throws SQLException {
        long start = System.nanoTime();
        List<Message> messages = MessageTable.find().find().toListAndClose();
        Log.d(TAG,
              "Read, " + messages.size()
                      + " rows");
        return System.nanoTime() - start;
    }

    @Override
    public long dropDb() throws SQLException {
        long start = System.nanoTime();
        List<JoogarDatabase> list = Joogar.getInstance().getDatabases();

        for(JoogarDatabase jd : list) {
            jd.close();
            jd.getPath().delete();
        }

        Joogar.getInstance().close();
        return System.nanoTime() - start;
    }

    @Override
    public String getOrmName() {
        return "Joogar new";
    }
}
