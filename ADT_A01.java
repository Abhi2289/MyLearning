package com.philips.CAPI.SanityTestcases;

import org.testng.annotations.Test;
import com.Philips.IBE.GenericFunction.GenericFunctions;
import java.io.File;
import org.testng.Assert;
import org.testng.Reporter;

public class ADT_A01 extends CAPISpecificFunctions {
	int waitingtime = 40;
	boolean MRNStatus = false;
	boolean LastNameStatus = false;
	boolean DeidentifyValueexist = false;
	boolean FirstNameStatus = false;
	boolean SSNStatus = false;
	boolean DeidentifyNameExist = false;
	boolean DeidentifyPIDExist = false;
	boolean DeidentifyVisitExist = false;

	@Test
	public void CAPI_ADT_A01() throws InterruptedException {
		String FolderPath = GenericFunctions.ReturnConfig("FolderPath");
		String Sourcepath = FolderPath + "/Repository\\CAPI_ADT_A01\\ADTA01.txt";
		String Destinationpath = FolderPath + "/ADT_ORM_DropLocation\\ADTA01.txt";

		String Sourcepath1 = FolderPath + "/Repository\\CAPI_ADT_A01\\Output\\ADTCloud.txt";
		String Sourcepath3 = FolderPath + "/Repository\\CAPI_ADT_A01\\Output\\ACK.txt";

		GenericFunctions.CopyFileFromOneLocationToAnotherLocation(Sourcepath, Destinationpath);

		File fnewFilePath = new File(Destinationpath);
		for (int j = 1; j < waitingtime; j++) {
			if (fnewFilePath.exists()) {
				Thread.sleep(1000);
			}
		}
		Assert.assertFalse(fnewFilePath.exists(),
				"File Is copied to Destination but not picked so please ADT_ORDER Proxy connection");

		Reporter.log("Step5.1.1-5.1.3:Verification for Acknowledgement");

		/*
		 * Acknowledgement verification on the basis of Message control ID
		 * MSH-10 AND MSA-2
		 */
		String MSGcontrolid = "/.MSH-10";
		String MessageControlID_Source = GenericFunctions.ReturnFieldValue(Sourcepath, MSGcontrolid);
		System.out.println("from MSH" + MessageControlID_Source);

		boolean AckResult = CAPISpecificFunctions.ValidateAcknowledgement(Sourcepath3, MessageControlID_Source);

		Assert.assertTrue(AckResult, "Acknowledgement is not processed properly");

		/*
		 * ************************************************************
		 * Validation for Patient MRN in HIF
		 */
		String FieldName = "/.PID-3-1";
		String Field_Value = GenericFunctions.ReturnFieldValue(Sourcepath, FieldName);
		String DesiredQuery = GenericFunctions.ReturnQuery("EXTRACTMRN");
		String ActualQuery = DesiredQuery + Field_Value + "'";

		Reporter.log("Step 5.1.4-5.1.5:Verification for Patient Demographic in DB");

		for (int initialwait = 0; initialwait <= waitingtime; initialwait++) {
			MRNStatus = GenericFunctions.ExcecuteSqlQuery(ActualQuery, Field_Value, "MedicalRecordNumber");
			if (MRNStatus == true) {
				Reporter.log("Patient is Available in the database");
				break;
			} else
				Thread.sleep(1000);
		}
		Assert.assertTrue(MRNStatus, "MRN not found in database");
		/*
		 * *********************************************************************
		 * Validation for Patient LastName in HIF
		 */
		String LastName = "/.PID-5-1";
		String LastName_Value = GenericFunctions.ReturnFieldValue(Sourcepath, LastName);
		String DesiredQuery2 = GenericFunctions.ReturnQuery("EXTRACTLASTNAME");
		String ActualQuery2 = DesiredQuery2 + LastName_Value + "'";

		LastNameStatus = GenericFunctions.ExcecuteSqlQuery(ActualQuery2, Field_Value, "MedicalRecordNumber");

		if (LastNameStatus == true)
			Reporter.log("Patient is Available with valid Lastname");
		else
			Reporter.log("Patient isnot Available with valid Lastname");

		Assert.assertTrue(LastNameStatus, "LastName not found in database");
		/*
		 * *********************************************************************
		 * Validation for Patient firstName in HIF
		 */
		String FirstName = "/.PID-5-2";
		String FirstName_Value = GenericFunctions.ReturnFieldValue(Sourcepath, FirstName);
		String DesiredQuery3 = GenericFunctions.ReturnQuery("EXTRACTFIRSTNAME");
		String ActualQuery3 = DesiredQuery3 + FirstName_Value + "'";

		FirstNameStatus = GenericFunctions.ExcecuteSqlQuery(ActualQuery3, Field_Value, "MedicalRecordNumber");

		if (FirstNameStatus == true)
			Reporter.log("Patient is Available with valid Firstname");
		else
			Reporter.log("Patient isnot Available with valid Firstname");

		Assert.assertTrue(FirstNameStatus, "LastName not found in database");

		/*
		 * *********************************************************************
		 * ***** Validation for SSN in HIF
		 */
		String SSN = "/.PID-19";
		String SSN_Value = GenericFunctions.ReturnFieldValue(Sourcepath, SSN);
		String DesiredQuery4 = GenericFunctions.ReturnQuery("EXTRACTSSN");
		String ActualQuery4 = DesiredQuery4 + SSN_Value + "'";

		SSNStatus = GenericFunctions.ExcecuteSqlQuery(ActualQuery4, Field_Value, "MedicalRecordNumber");

		if (SSNStatus == true)
			Reporter.log("Patient is Available with valid SSN");
		else
			Reporter.log("Patient is not Available with valid SSN");

		Assert.assertTrue(FirstNameStatus, "SSN not found in database");

		/*
		 * *********************************************************************
		 * ********* Deidentification Validation for Details in SIS Database
		 */
		Reporter.log("Step5.1.6-:Verification for Deidentification in SIS Database");

		String DeidentifyMRN = GenericFunctions.ReturnFieldValue(Sourcepath1, FieldName);

		String DesiredQuery1 = GenericFunctions.ReturnQuery("DeidentifyQuery");
		String ActualQuery1 = DesiredQuery1 + DeidentifyMRN + "'";
		

		for (int initialwait = 0; initialwait <= waitingtime; initialwait++) {
			DeidentifyValueexist = GenericFunctions.ExcecuteSqlQuery(ActualQuery1, DeidentifyMRN,
					"DeidentifiedPatientNumber");
			if (DeidentifyValueexist == true) {
				Reporter.log("Patient DeidentifyMRN is Available in the SISdatabase");
				break;
			} else
				Thread.sleep(1000);
		}
		Assert.assertTrue(DeidentifyValueexist, "DeidentifyMRN not found in SISdatabase");

		/*
		 * *********************************************************************
		 * *********** Deidentified Name validation
		 */

		String DeidentifyName = GenericFunctions.ReturnFieldValue(Sourcepath1, LastName);
		String DesiredQuery5 = GenericFunctions.ReturnQuery("DeidentifyQuery");
		String ActualQuery5 = DesiredQuery5 + DeidentifyName + "'";
		
		System.out.println(ActualQuery5);

		DeidentifyNameExist = GenericFunctions.ExcecuteSqlQuery(ActualQuery5, DeidentifyName,
				"DeidentifiedPatientNumber");

		if (DeidentifyNameExist == true)
			Reporter.log("Patient DeidentifyName is Available in the SISdatabase");

		else
			Reporter.log("Patient DeidentifyName is not Available in the SISdatabase");

		Assert.assertTrue(DeidentifyNameExist, "DeidentifyName not found in SISdatabase");

		/*
		 * *********************************************************************
		 * ********* Deidentify PID
		 */
		String PID = "/.PID-2-1";
		String DeidentifyPID = GenericFunctions.ReturnFieldValue(Sourcepath1, PID);
		String DesiredQuery6 = GenericFunctions.ReturnQuery("DeidentifyQuery");
		String ActualQuery6 = DesiredQuery6 + DeidentifyPID + "'";
		System.out.println(ActualQuery6);
		
		DeidentifyPIDExist = GenericFunctions.ExcecuteSqlQuery(ActualQuery6, DeidentifyPID,
				"DeidentifiedPatientNumber");

		if (DeidentifyPIDExist == true)
			Reporter.log("Patient DeidentifyPID is Available in the SISdatabase");

		else
			Reporter.log("Patient DeidentifyPID is not Available in the SISdatabase");

		Assert.assertTrue(DeidentifyPIDExist, "DeidentifyName not found in SISdatabase");

		/*
		 * *********************************************************************
		 * ************ Deidentify Visit Number
		 */
		String VisitNumber = "/.PV1-19-1";
		String DeidentifyVisitNumber = GenericFunctions.ReturnFieldValue(Sourcepath1, VisitNumber);
		String DesiredQuery7 = GenericFunctions.ReturnQuery("DeidentifyQuery");
		String ActualQuery7 = DesiredQuery7 + DeidentifyVisitNumber + "'";
		DeidentifyVisitExist = GenericFunctions.ExcecuteSqlQuery(ActualQuery7, DeidentifyVisitNumber,
				"DeidentifiedPatientNumber");

		if (DeidentifyVisitExist == true)
			Reporter.log("Patient DeidentifyVisitNumber is Available in the SISdatabase");

		else
			Reporter.log("Patient DeidentifyVisitNumber is not Available in the SISdatabase");

		Assert.assertTrue(DeidentifyVisitExist, "DeidentifyName not found in SISdatabase");

		/*
		 * *********************************************************************
		 * ************* Validation for removal of DOB before reaching to Cloud
		 */
		Reporter.log("Step5.1.7:Verification for Removal of HomePhone and DOB");
		String DOB = "/.PID-7-1";
		String DOB_Cloud = GenericFunctions.ReturnFieldValue(Sourcepath1, DOB);
		Assert.assertEquals(DOB_Cloud, null);

		/*
		 * *********************************************************************
		 * ************** Validation for Removal of Home Phone Number before
		 * reaching to cloud
		 */
		String HomePhone = "/.PID-13-1";
		String HomePhone_Cloud = GenericFunctions.ReturnFieldValue(Sourcepath1, HomePhone);
		Assert.assertEquals(HomePhone_Cloud, null);

		/*
		 * *********************************************************************
		 * ************* Verification for Facility getting removed before
		 * reaching to cloud
		 */

		String Facility = "/.MSH-4-1";
		String SendingFacility = GenericFunctions.ReturnFieldValue(Sourcepath1, Facility);
		Assert.assertEquals(SendingFacility, null);

		/*
		 * before reaching to hiF it should get removed
		 * *********************************************************************
		 * ************* Verification for Room and Bed getting removed before
		 * reaching to ICCA
		 */

		String CareUnit = "/.PV1-3-1";
		String Sourcepath2 = FolderPath + "/Repository\\CAPI_ADT_A01\\Output\\ADTICCA.txt";
		String Field_Value_CareUnit = GenericFunctions.ReturnFieldValue(Sourcepath2, CareUnit);
		Assert.assertEquals(Field_Value_CareUnit, "1_1");

	}

}
