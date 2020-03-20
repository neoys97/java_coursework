import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

/**
 * FilePicker class
 * @author Neo Yi Siang
 */
public class FilePicker {
	
	/**
	 * prompt a file picker and return the path of the file
	 * @return path of the file
	 */
	public static String getFilePath() {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			return (selectedFile.getAbsolutePath().toString());
		}
		
		return ("");

	}
	
}
