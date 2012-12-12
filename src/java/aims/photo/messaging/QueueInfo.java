package aims.photo.messaging;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 7/08/2008
 * Time: 13:02:17
 * To change this template use File | Settings | File Templates.
 */
public class QueueInfo {
    private Queue queue;
    private QueueConnection queueConnection;
    private QueueSession session;

    public QueueInfo(Queue queue, QueueConnection queueConnection, QueueSession session) {
        this.queue = queue;
        this.queueConnection = queueConnection;
        this.session = session;
    }

    public QueueSession getSession() {
        return session;
    }

    public void setSession(QueueSession session) {
        this.session = session;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public QueueConnection getQueueConnection() {
        return queueConnection;
    }

    public void setQueueConnection(QueueConnection queueConnection) {
        this.queueConnection = queueConnection;
    }
}
