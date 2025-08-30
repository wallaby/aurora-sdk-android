package heatshrink;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by mriley on 12/10/16.
 */
@RunWith(Parameterized.class)
public class HsOutputStreamFileTest {

	@Parameters(name="{0}")
	public static Iterable<Object[]> generateParameters() {
		return TestData.getTestParameters();
	}

	private final TestFile testFile;

	public HsOutputStreamFileTest(TestFile testFile) {
		this.testFile = testFile;
	}

	@Test
	public void testWrite() throws IOException {
		byte[] compressed = FileUtils.readFileToByteArray(testFile.getCompressed());
		byte[] uncompressed = FileUtils.readFileToByteArray(testFile.getUncompressed());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try(HsOutputStream hso = new HsOutputStream(output, testFile.getWindowSize(), testFile.getLookaheadSize())) {
			hso.write(uncompressed);
		}
		byte[] compressed2 = output.toByteArray();
		Assert.assertArrayEquals(compressed2, compressed);
	}
}
