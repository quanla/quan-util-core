package qj.util;

import java.io.IOException;
import java.net.Socket;

import qj.util.funct.P0;
import qj.util.funct.P2;

/**
 * @author quanle
 *
 */
public class SocketUtil {
    public static P0 connect(final Socket sk1, final Socket sk2) {
        return connect(sk1, sk2, null);
    }

    public static P0 connect(final Socket sk1, final Socket sk2, final P0 afterCloseF) {
    	return connect(sk1, sk2, null, null, afterCloseF);
    }
    public static P0 connect(final Socket sk1, final Socket sk2, final P2<byte[],Integer> bytes12P, final P2<byte[],Integer> bytes21P, final P0 afterCloseF) {
        final boolean[] closed = {false};
        P0 closeF = new P0() {
            public void e() {
                if (!closed[0]) {
                    closed[0] = true;
                    close(sk1, sk2);
                    if (afterCloseF != null) {
                        afterCloseF.e();
                    }
                }
            }
        };
        ThreadUtil.run(connect1(sk1, sk2, bytes12P, closeF));
        ThreadUtil.run(connect1(sk2, sk1, bytes21P, closeF));
        return closeF;
    }

    private static Runnable connect1(final Socket sk1, final Socket sk2, final P2<byte[],Integer> bytesP, final P0 closeF) {
        return new Runnable() {public void run() {
            try {
                IOUtil.connect(sk1.getInputStream(), sk2.getOutputStream(), bytesP);
            } catch (IOException e) {
                if (!"socket closed".equals(e.getMessage().toLowerCase())) {
                    e.printStackTrace();
                }
            } finally {
                closeF.e();
            }

        }};

    }

    private static void close(Socket... sks) {
        for (Socket sk : sks) {
            //noinspection EmptyCatchBlock
            try {
                if (sk != null) {
                    sk.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
