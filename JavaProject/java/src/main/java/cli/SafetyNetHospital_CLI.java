package cli;


import SafetyNetHospital.*;
import SafetyNetHospital.quotes.*;
import cli.menus.*;
import cli.vdm_enum.AGREEMENT;
import cli.vdm_enum.SPECIALTY;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.overture.codegen.runtime.VDMMap;
import org.overture.codegen.runtime.VDMSet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class SafetyNetHospital_CLI {

    public static void main(String[] args) {
        SafetyNetHospital_CLI safetyNet = new SafetyNetHospital_CLI();
    }

    private SafetyNetNetwork safetyNet;
    private TextIO textIO;
    private TextTerminal terminal;

    public SafetyNetHospital_CLI() {
        this.safetyNet = SafetyNetNetwork.getInstance();
        this.textIO = TextIoFactory.getTextIO();
        this.terminal = textIO.getTextTerminal();

        VDMSet agreement = new VDMSet();
        agreement.add(getAgreement(AGREEMENT.ADSE));
        agreement.add(getAgreement(AGREEMENT.MEDIS));
        safetyNet.addHospital(new Hospital("Sao Joao",new ModelUtils.Location("Porto","rua x n y","4520-885"), agreement));
        VDMSet agreement1 = new VDMSet();
        agreement1.add(getAgreement(AGREEMENT.MULTICARE));
        agreement1.add(getAgreement(AGREEMENT.MEDICARE));
        safetyNet.addHospital(new Hospital("Santa Maria",new ModelUtils.Location("Lisboa","Rua x blablabla","4880-724"),agreement1));


        safetyNet.addDoctor(new Doctor("Jose",20,getSpecialty(SPECIALTY.ORTOPEDIA)));
        safetyNet.addDoctor(new Doctor("Marcelo",19,getSpecialty(SPECIALTY.CARDIOLOGIA)));

        safetyNet.addPatient(new Patient("Maria",14,"gripe"));
        safetyNet.addPatient(new Patient("Joana",20,"tosse"));
        safetyNet.addPatient(new Patient("Felizberta",22,"asma"));



        this.start();
    }

    public Object getSpecialty(SPECIALTY specialty){
        switch (specialty) {
            case ORTOPEDIA:
                return new ORTOPEDIAQuote();
            case CARDIOLOGIA:
                return new CARDIOLOGIAQuote();
            case OFTALMOLOGIA:
                return new OFTALMOLOGIAQuote();
            case DERMATOLOGIA:
                return new DERMATOLOGIAQuote();
            case GINECOLOGIA:
                return new GINECOLOGIAQuote();
            case NEUROLOGIA:
                return new NEUROLOGIAQuote();
            case PEDIATRIA:
                return new PEDIATRIAQuote();
            case REUMATOLOGIA:
                return new REUMATOLOGIAQuote();
            case UROLOGIA:
                return new UROLOGIAQuote();
            case PNEUMOLOGIA:
                return new PNEUMOLOGIAQuote();
        }

        return null;
    }

    public Object getAgreement(AGREEMENT agreement){
        switch (agreement) {
            case ADSE:
                return new ADSEQuote();
            case MEDICARE:
                return new MEDICAREQuote();
            case MEDIS:
                return new MEDISQuote();
            case MULTICARE:
                return new MULTICAREQuote();
            case NONE:
                break;
        }
        return null;
    }

    public void start(){
        separator();
        this.terminal.printf("-----------------------------------------------------------------------\n" +
                             "----------------------Safety Net Hospital---------------------\n" +
                             "-----------------------------------------------------------------------\n"
        );

        START_MENU val = textIO.newEnumInputReader(START_MENU.class)
                .read("Select an option!");

        switch (val){
            case HOSPITALS:
                hospitals();
                break;
            case DOCTORS:
                doctors();
                break;
            case APPOINTMENTS:
                appointments();
                break;
            case PATIENTS:
                patients();
                break;
            case EXIT:
            default:
                System.exit(0);
                break;
        }
    }

    public void hospitals(){
        separator();
        this.terminal.printf("--------------------------Hospitals Management--------------------------\n");

        HOSPITAL_MENU val = textIO.newEnumInputReader(HOSPITAL_MENU.class)
                .read("Select an option!");
        int i = 0;

        switch (val){

            case ASSOCIATE_DOCTOR:

                Number hosId = getMapSelectedElement(safetyNet.getHospitals());
                separator();
                Number docId = getMapSelectedElement( safetyNet.getDoctors());

                if(hosId.intValue() == -1 || docId.intValue() == -1)
                    hospitals();

                safetyNet.associateDoctorToHospital(hosId,docId);
                hospitals();
                break;
            case DISASSOCIATE_DOCTOR:

                Number hospId = getMapSelectedElement(safetyNet.getHospitals());
                separator();
                Hospital h = safetyNet.getHospitalsById(hospId);
                List<Integer> list = new ArrayList<Integer>(h.getDoctorsIds());
                HashMap<Number,Object> hospitalDocs =new HashMap();

                for (int j = 0; j < list.size(); j++) {
                   hospitalDocs.put(list.get(j),safetyNet.getDoctorById(list.get(j)));
                }

                Number doctId = getMapSelectedElement(hospitalDocs);

                if(hospId.intValue() == -1 || doctId.intValue() == -1)
                    hospitals();

                safetyNet.disassociateDoctorFromHospital(hospId,doctId);
                hospitals();
                break;
            case ADD:
                String name = textIO.newStringInputReader()
                        .read("Hospital Name");

                String city = textIO.newStringInputReader()
                        .read("City");

                String address = textIO.newStringInputReader()
                        .read("Address");

                String postalCode = textIO.newStringInputReader()
                        .read("Postal-Code");

                VDMSet agreements = new VDMSet();
                boolean parsingAgreements = true;
                do {

                AGREEMENT agreement = textIO.newEnumInputReader(AGREEMENT.class)
                        .read("Select an option!");

                if (agreement == AGREEMENT.NONE)
                    parsingAgreements=false;
                else
                    agreements.add(getAgreement(agreement));
                }while(parsingAgreements);

                safetyNet.addHospital(new Hospital(name,new ModelUtils.Location(city,address,postalCode),agreements));
                hospitals();

                break;
            case REMOVE:
                HashMap<Number,Object> m = safetyNet.getHospitals();
                Number rmId = getMapSelectedElement(m);
                if(rmId.intValue() == -1)
                    hospitals();
                else {
                    safetyNet.removeHospital((Hospital) m.get(rmId));
                    hospitals();
                }
                break;
            case SEARCH: //TODO: search by property
                this.terminal.printf("--------------------------Hospitals--------------------------\n");
                i=0;
                for (Map.Entry<Number,Hospital> entry : new HashMap<Number,Hospital>(safetyNet.getHospitals()).entrySet())
                {
                    this.terminal.printf(i +  "- \n" +  entry.getValue() + "\n");
                    i++;
                }

                BACK v = textIO.newEnumInputReader(BACK.class)
                        .read("Select an option!");

                this.terminal.printf("\n\n");

                switch (v) {
                    case BACK:
                        hospitals();
                        break;
                }

                    break;
            case BACK:
                start();
                break;
            case EXIT:
            default:
                System.exit(0);
                break;
        }
    }

    public Number getMapSelectedElement(HashMap<Number,Object> map){

        List<Integer> values= new ArrayList<Integer>();

        int i=0;
        for (Map.Entry<Number,Object> entry : map.entrySet())
        {
            this.terminal.printf( i + "- \n " +  entry.getValue() + "\n");

            values.add(i);
            i++;
        }

        if(values.size() == 0) {
            this.terminal.printf( "\n\n --------------- No available Items ------------- \n\n\n");
            BACK v = textIO.newEnumInputReader(BACK.class)
                    .read("Select an option!");
            return -1;
        }

        int rmId = textIO.newIntInputReader()
                .withInlinePossibleValues(values)
                .read("Key");



        return (Number) map.keySet().toArray()[rmId];
    }


    public void doctors(){
        separator();
        this.terminal.printf("--------------------------Doctors Management--------------------------\n");

        DOCTOR_MENU val = textIO.newEnumInputReader(DOCTOR_MENU.class)
                .read("Select an option!");

        switch (val){

            case ADD:
                String user = textIO.newStringInputReader()
                        .read("Name");

                int age = textIO.newIntInputReader()
                        .withMinVal(18)
                        .read("Age");


                SPECIALTY specialty = textIO.newEnumInputReader(SPECIALTY.class)
                        .read("Select an option!");

                safetyNet.addDoctor(new Doctor(user,age,getSpecialty(specialty)));

                doctors();
                break;
            case REMOVE:
                HashMap<Number,Object> m = safetyNet.getDoctors();
                Number rmId = getMapSelectedElement(m);
                if(rmId.intValue() == -1)
                    doctors();
                else {
                    safetyNet.removeDoctor((Doctor) m.get(rmId));
                    doctors();
                }

                break;
            case SEARCH:
                searchDoctor();
                break;
            case BACK:
                start();
                break;
            case EXIT:
            default:
                System.exit(0);
                break;
        }
    }

    public void searchDoctor(){
        separator();
        this.terminal.printf("--------------------------Search Doctors--------------------------\n");

        SEARCH_OPTIONS_MENU val = textIO.newEnumInputReader(SEARCH_OPTIONS_MENU.class)
                .read("Select an option!");

        switch (val){

            case SEE_ALL:
                HashMap<Number,Doctor> map = new HashMap(safetyNet.getDoctors());

                for (Map.Entry<Number,Doctor> entry : map.entrySet())
                {
                    this.terminal.printf("--------------------------Doctors--------------------------\n");
                    this.terminal.printf("key:" + entry.getKey() + " -- Data: " +  entry.getValue() + "\n");
                }
                break;
            case BY_NAME:
                break;
            case BY_SPECIALTY:
                break;
            case BACK:
                doctors();
                break;
            case EXIT:
            default:
                System.exit(0);
                break;
        }
    }


    public void appointments(){
        separator();
        this.terminal.printf("--------------------------Appointments Management--------------------------\n");

        APPOINTMENTS_MENU val = textIO.newEnumInputReader(APPOINTMENTS_MENU .class)
                .read("Select an option!");

        switch (val){
            case ADD:
                break;
            case REMOVE:
                break;
            case SEE:
                break;
            case GET_FIRST_AVAILABLE_DATE_FOR_AN_APPOINTMENT:
                break;
            case BACK:
                start();
                break;
            case EXIT:
            default:
                System.exit(0);
                break;
        }
    }


    public void patients(){
        separator();
        this.terminal.printf("--------------------------Patients Management--------------------------\n");

        PATIENTS_MENU val = textIO.newEnumInputReader(PATIENTS_MENU.class)
                .read("Select an option!");

        switch (val){
            case ADD:
                String user = textIO.newStringInputReader()
                        .read("Name");

                int age = textIO.newIntInputReader()
                        .withMinVal(18)
                        .read("Age");


                String observation = textIO.newStringInputReader()
                        .read("Observation");

                safetyNet.addPatient(new Patient(user,age,observation));

                patients();
                break;
            case REMOVE:
                HashMap<Number,Object> m = safetyNet.getPatients();
                Number rmId = getMapSelectedElement(m);
                if(rmId.intValue() == -1)
                    patients();
                else {
                    safetyNet.removePatient((Patient) m.get(rmId));
                    patients();
                }
                break;
            case SEARCH:
                start();
            case BACK:
                start();
                break;
            case EXIT:
                System.exit(0);
                break;
        }
    }

    public void separator(){
        this.terminal.printf("------------------------------------------------------------------------------\n" +
                             "------------------------------------------------------------------------------\n" +
                             "------------------------------------------------------------------------------\n");
    }


}
