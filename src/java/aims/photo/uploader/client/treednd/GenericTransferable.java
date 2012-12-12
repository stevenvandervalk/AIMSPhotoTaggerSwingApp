package aims.photo.uploader.client.treednd;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GenericTransferable implements Transferable {
    public GenericTransferable(Object data) {
        super();
        this.data = data;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        return data;
    }

    private Object data;
    private static final DataFlavor[] flavors = new DataFlavor[1];

    static {
        flavors[0] = DataFlavor.stringFlavor;
    }
}
