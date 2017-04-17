package com.littleinc.orm_benchmark.joogar_old;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.littleinc.orm_benchmark.BenchmarkExecutable;


import net.skoumal.joogar.shared.Joogar;
import net.skoumal.joogar.shared.JoogarDatabase;
import net.skoumal.joogar.shared.JoogarDatabaseBuilder;
import net.skoumal.joogar.shared.JoogarRecord;

import java.util.LinkedList;
import java.util.List;

import static com.littleinc.orm_benchmark.util.Util.getRandomString;

/**
 * Created by Jan Bína on 04/17/2017
 */
public class JoogarOldExecutor implements BenchmarkExecutable
{

    private static final String TAG = "JoogarOldExecutor";
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

        JoogarDatabaseBuilder builder = new JoogarDatabaseBuilder()
                .setDomainClasses(User.class, Message.class)
                .setVersion(1);

        Joogar.initForAndroid(applicationContext).addDB(builder);

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

        try {
            Joogar.getInstance().getDB().openTransaction();
            for (User user : users) {
                JoogarRecord.save(user);
            }
            Log.d(TAG, "Done, wrote " + NUM_USER_INSERTS + " users");

            for (Message message : messages) {
                JoogarRecord.save(message);
            }
            Log.d(TAG, "Done, wrote " + NUM_MESSAGE_INSERTS + " messages");
            Joogar.getInstance().getDB().commitTransaction();
        } catch (Exception e) {
            Joogar.getInstance().getDB().rollbackTransaction();
            throw new RuntimeException(e);
        }

        return System.nanoTime() - start;
    }

    @Override
    public long readWholeData() throws SQLException {
        long start = System.nanoTime();
        List<Message> messages = JoogarRecord.findAll(Message.class).toListAndClose();
        Log.d(TAG,
              "Read, " + messages.size()
                      + " rows");
        return System.nanoTime() - start;
    }

    @Override
    public long dropDb() throws SQLException {
        long start = System.nanoTime();
        List<JoogarDatabase> list = Joogar.getInstance().getDBList();

        for(JoogarDatabase jd : list) {
            jd.close();
            jd.getPath().delete();
        }

        Joogar.getInstance().close();
        return System.nanoTime() - start;
    }

    @Override
    public String getOrmName() {
        return "Joogar old";
    }
}
