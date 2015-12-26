package cz.hatoff.bbn.configuration;

import cz.hatoff.bbn.configuration.xsd.Configuration;
import cz.hatoff.bbn.security.Encrypter;
import cz.hatoff.bbn.security.EncrypterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class ConfigurationBean {

    private static ConfigurationBean configurationBean;

    private static final Logger logger = LogManager.getLogger(Configuration.class);
    public static final String ENCRYPTED_PASSWORD_PREFIX = "DES:";

    private Encrypter encrypter = new Encrypter();

    private final static String CONFIGURATION_FILE_PATH = "configuration.xml";
    private Configuration configuration;

    private ConfigurationBean(){
        File configurationFile = new File(CONFIGURATION_FILE_PATH);
        logger.info("Loading configuration from file  '" + configurationFile.getAbsolutePath() + "'.");
        if (configurationFile.exists()){
            configuration = readJaxbConfiguration(configurationFile, Configuration.class);
            checkIfPasswordEncrypted();
        } else {
            logger.error("Configuration file '" + configurationFile.getAbsolutePath() + "' does not exists.");
            System.exit(1);
        }
    }

    private void checkIfPasswordEncrypted() {
        String password = configuration.getPassword();
        if (password != null) {
            if (!password.startsWith(ENCRYPTED_PASSWORD_PREFIX)) {
                logger.info("Unencrypted password found in condiguration file.");
                encryptPassword();
            }
        } else {
            logger.error("Failed to read password from configuration.");
            System.exit(1);
        }
    }

    private void encryptPassword() {
        try {
            logger.info("Going to encrypt password in configuration file.");
            String encryptedPassword = encrypter.encrypt(configuration.getPassword());
            configuration.setPassword(ENCRYPTED_PASSWORD_PREFIX + encryptedPassword);
            writeChangesToConfiguration();
        } catch (EncrypterException e) {
            logger.error("Failed to encrypt password.");
            System.exit(1);
        }
    }

    private <T> T readJaxbConfiguration(File configurationFile, Class<T> classT){
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classT);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(configurationFile);
        } catch (JAXBException e) {
            logger.error("Failed to read configuration file '" + configurationFile.getAbsolutePath() + "'", e);
            System.exit(1);
        }
        return null;
    }

    public void writeChangesToConfiguration(){
        File file = new File(CONFIGURATION_FILE_PATH);
        logger.info("Going to update configuration file '" + file.getAbsolutePath() + "'.");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(configuration, file);
            jaxbMarshaller.marshal(configuration, System.out);
        } catch (JAXBException e) {
            logger.error("Failed to write to configuration file '" + file.getAbsolutePath() + "'", e);
            System.exit(1);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static ConfigurationBean getInstance() {
        if (configurationBean == null) {
            configurationBean = new ConfigurationBean();
        }
        return configurationBean;
    }

    public String getDecryptedPassword() {
        String password = configuration.getPassword();
        if (password.startsWith(ENCRYPTED_PASSWORD_PREFIX)) {
            try {
                password = encrypter.decrypt(password.replaceFirst(ENCRYPTED_PASSWORD_PREFIX, ""));
            } catch (EncrypterException e) {
                throw new RuntimeException("Failed to decrypt password.", e);
            }
        }
        return password;
    }
}
