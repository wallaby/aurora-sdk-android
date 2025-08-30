package heatshrink;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mriley on 12/3/16.
 */
@RunWith(Parameterized.class)
public class HsInputStreamFileTest {

	@Parameters(name="{0}")
	public static Iterable<Object[]> generateParameters() {
		return TestData.getTestParameters();
	}

	private final TestFile testFile;

	public HsInputStreamFileTest(TestFile testFile) {
		this.testFile = testFile;
	}

	@Test
	public void testReadIntoLargeBuffer() throws IOException {
		byte[] compressed = FileUtils.readFileToByteArray(testFile.getCompressed());
		byte[] uncompressed = FileUtils.readFileToByteArray(testFile.getUncompressed());
		try(HsInputStream hsi = new HsInputStream(new ByteArrayInputStream(compressed), testFile.getWindowSize(), testFile.getLookaheadSize())) {
			int btr = (int) testFile.getUncompressed().length();
			byte[] uncompressed2 = new byte[btr*2];
			int bytesRead = IOUtils.read(hsi, uncompressed2);
			Assert.assertEquals(new String(uncompressed2), uncompressed.length, bytesRead);
			byte[] uncompressed3 = new byte[btr];
			System.arraycopy(uncompressed2, 0, uncompressed3, 0, uncompressed.length);
			Assert.assertArrayEquals(new String(uncompressed3), uncompressed3, uncompressed);
		}
	}

	@Test
	public void testReadIntoSmallBuffer() throws IOException {
		byte[] compressed = FileUtils.readFileToByteArray(testFile.getCompressed());
		byte[] uncompressed = FileUtils.readFileToByteArray(testFile.getUncompressed());
		try(HsInputStream hsi = new HsInputStream(new ByteArrayInputStream(compressed), testFile.getWindowSize(), testFile.getLookaheadSize())) {
			int btr = (int) testFile.getUncompressed().length();
			byte[] uncompressed2 = new byte[7];
			byte[] uncompressed3 = new byte[btr];
			int bytesRead = 0;
			int total = 0;
			while((bytesRead = IOUtils.read(hsi, uncompressed2)) > 0) {
				System.arraycopy(uncompressed2, 0, uncompressed3, total, bytesRead);
				total += bytesRead;
			}
			Assert.assertArrayEquals(new String(uncompressed3), uncompressed3, uncompressed);
		}
	}

	@Test
	public void readIndividualBytes() throws IOException {
		byte[] compressed = FileUtils.readFileToByteArray(testFile.getCompressed());
		byte[] uncompressed = FileUtils.readFileToByteArray(testFile.getUncompressed());
		try(HsInputStream hsi = new HsInputStream(new ByteArrayInputStream(compressed), testFile.getWindowSize(), testFile.getLookaheadSize())) {
			long i;
			for(i = 0; hsi.read() != -1; i++);
			Assert.assertEquals(uncompressed.length, i);
		}
	}

	@Test
	public void testSkip() throws IOException {
		byte[] compressed = FileUtils.readFileToByteArray(testFile.getCompressed());
		byte[] uncompressed = FileUtils.readFileToByteArray(testFile.getUncompressed());
		try(HsInputStream hsi = new HsInputStream(new ByteArrayInputStream(compressed), testFile.getWindowSize(), testFile.getLookaheadSize())) {
			long skipped = hsi.skip(uncompressed.length);
			Assert.assertEquals(uncompressed.length, skipped);
		}
	}
}
