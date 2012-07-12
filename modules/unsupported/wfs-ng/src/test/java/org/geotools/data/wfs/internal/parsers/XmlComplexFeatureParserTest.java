package org.geotools.data.wfs.internal.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.geotools.feature.FakeTypes;
import org.geotools.feature.MineType;
import org.geotools.feature.wrapper.FeatureWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

public class XmlComplexFeatureParserTest {

	@Test
	public void demo() throws IOException {
		// This is creating the XmlComplexFeatureParser by manually passing it a file stream
		// of a WFS response. The targetType and featureDescriptorName are hard-coded for the
		// sample. The parser would normally be instantiated in WFSContentDataAccess with the
		// types that it had extracted with EmfAppSchemaReader.
		mineParser = new XmlComplexFeatureParser(
			/* getFeatureResponseStream: */ getResourceAsFileInputStream("sample_wfs_response_two_mines.xml"),
			/* targetType:				 */ FakeTypes.Mine.MINETYPE_TYPE,
			/* featureDescriptorName:	 */ FakeTypes.Mine.NAME_Mine);
		Feature feature = mineParser.parse();
		MineType mine = FeatureWrapper.Wrap(feature, MineType.class);
//		System.out.println("getPreferredName: " + mine.getPreferredName());
	}
	
//	@Test
//	public void demo() throws IOException {
//		// This is creating the XmlComplexFeatureParser by manually passing it a file stream
//		// of a WFS response. The targetType and featureDescriptorName are hard-coded for the
//		// sample. The parser would normally be instantiated in WFSContentDataAccess with the
//		// types that it had extracted with EmfAppSchemaReader.
//		mineParser = new XmlComplexFeatureParser(
//			/* getFeatureResponseStream: */ getResourceAsFileInputStream("sample_wfs_response_two_mines_internal_href.xml"),
//			/* targetType:				 */ FakeTypes.Mine.MINETYPE_TYPE,
//			/* featureDescriptorName:	 */ FakeTypes.Mine.NAME_Mine);
//		
//		Feature feature = mineParser.parse();
//		MineType mine = FeatureWrapper.Wrap(feature, MineType.class);
//		
//		System.out.println(mine.MineNameProperties.get(0).MineName.mineName);
//		System.out.println(mine.getPreferredName());
//	}

	/**
	 * This method gets a file input stream for the file name specified.
	 * It looks for the file in /org/geotools/data/wfs/internal/parsers/test-data/
	 * and will Assert.fail() if it's not there.  
	 * @param resourceName 
	 * 		The name of the file whose stream you want. (Must be in test-data folder)
	 * @return
	 * 		A FileInputStream of the file you requested. 
	 */
	private FileInputStream getResourceAsFileInputStream(String resourceName) {
		final URL url = getClass().getResource("/org/geotools/data/wfs/internal/parsers/test-data/" + resourceName);

		try {
			return new FileInputStream(new File(url.getPath()));
		}
		catch (FileNotFoundException fnfe) {
			Assert.fail("Could not find the file '" + resourceName + "'.");
		}
		
		// I don't think this is actually reachable because the Assert.fail() 
		// should cause the method to terminate but it has to be here to compile.
		return null;
	}

	private static void writeln(String message) {
		write(message + String.format("%n"));
	}
	
	private static void write(String message) {
		char[] tabs = new char[explodeFeatureDepth * 4];		
		
		for (int i = 0; i < tabs.length; i++) {
 			tabs[i] = ' ';
		}

		String whitespace = new String(tabs);
		System.out.print(whitespace + message);
	}
	
	private static int explodeFeatureDepth = 0;
	
	private static void explodeProperty(Property property) {
		write(property.getName().getLocalPart() + ":");//" (" + property.getClass().getSimpleName() + "):");
		explodeFeatureDepth++;

		if (ComplexAttribute.class.isAssignableFrom(property.getClass())) {
			System.out.println();
			for (Property subProperty : ((ComplexAttribute)property).getProperties()) {
				explodeProperty(subProperty);
				explodeFeatureDepth--;
			}
		}
		else if (Attribute.class.isAssignableFrom(property.getClass())) {
			System.out.println(" " + ((Attribute)property).getValue());
		}
		else {
			writeln("! " + property.getClass().getSimpleName());
		}
	}
	
	private XmlComplexFeatureParser mineParser; 
	
	@Before
	public void setUp() {

		String inputFileName = "sample_wfs_response_two_mines.xml";
		
		try
		{
			mineParser = new XmlComplexFeatureParser(
				getResourceAsFileInputStream(inputFileName),
				FakeTypes.Mine.MINETYPE_TYPE,
				FakeTypes.Mine.NAME_Mine);
		}
		catch (IOException ioe) {
			Assert.fail("Can't find " + inputFileName);
		}
	}
	
	@After
	public void tearDown() throws IOException {
		this.mineParser.close();
		this.mineParser = null;
	}

	@Test
	public void getNumberOfFeatures_WFSResponseWithTwoFeatures_returns2() throws IOException {
		// Arrange
		InputStream stream = getResourceAsFileInputStream("sample_wfs_response_two_mines.xml");

        XmlComplexFeatureParser parser = new XmlComplexFeatureParser(
        	stream,
        	FakeTypes.Mine.MINETYPE_TYPE,
        	FakeTypes.Mine.NAME_Mine);

        // Act
        int numberOfFeatures = parser.getNumberOfFeatures();

        // Assert
        Assert.assertEquals(2, numberOfFeatures);
	}
	
	/**
	 * This test has a pretty brute-force assertion which makes it kind of brittle. Other tests  
	 * are more fine-grained in their approach, checking that specific things have been set.
	 * @throws IOException
	 */
	@Test
	public void parse_firstMine_returnsAdmiralHill() throws IOException {
        // Act
        Feature feature = mineParser.parse();

        // explodeProperty(feature);

        // Assert
        Assert.assertEquals(
			"FeatureImpl:MineType<MineType id=er.mine.S0000001>=[ComplexAttributeImpl:MineNamePropertyType=[ComplexAttributeImpl:MineName<MineNameType id=MINENAMETYPE_TYPE_1>=[ComplexAttributeImpl:MineNameType=[AttributeImpl:isPreferred<boolean id=isPreferred_1>=true, AttributeImpl:mineName<string id=mineName_1>=Pieces of Eight - Admiral Hill]]], ComplexAttributeImpl:MineNamePropertyType=[ComplexAttributeImpl:MineName<MineNameType id=MINENAMETYPE_TYPE_2>=[ComplexAttributeImpl:MineNameType=[AttributeImpl:isPreferred<boolean id=isPreferred_2>=false, AttributeImpl:mineName<string id=mineName_2>=Admiral Hill S - W Shear (WAMIN)]]]]",
			feature.toString());
	}
	
	/**
	 * This test has a pretty brute-force assertion which makes it kind of brittle. Other tests  
	 * are more fine-grained in their approach, checking that specific things have been set.
	 * @throws IOException
	 */
	@Test
	public void parse_secondMine_returnsAspacia() throws IOException {
		// Arrange
		mineParser.parse(); // This gets rid of the first result. 

        // Act
        Feature feature = mineParser.parse();

        // Assert
        Assert.assertEquals(
			"FeatureImpl:MineType<MineType id=er.mine.S0000005>=[ComplexAttributeImpl:MineNamePropertyType=[ComplexAttributeImpl:MineName<MineNameType>=[ComplexAttributeImpl:MineNameType=[AttributeImpl:isPreferred<boolean>=true, AttributeImpl:mineName<string>=Aspacia]]]]",
			feature.toString());
	}
}




































