package cz.hatoff.bbn.bamboo.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.hatoff.bbn.bamboo.model.FavouriteBuildResponse;
import cz.hatoff.bbn.configuration.ConfigurationBean;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class BambooClient {

    private static final Logger logger = LogManager.getLogger(BambooClient.class);
    private static final String API_PATH = "/rest/api/latest";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final URI serverUri;

    private CloseableHttpClient httpClient;

    private ConfigurationBean configurationBean = ConfigurationBean.getInstance();

    public BambooClient(URL serverUrl) {
        this.serverUri = parseServerUri(serverUrl);
        CredentialsProvider credentialsProvider = getCredentialsProvider(serverUrl);
        httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }

    private CredentialsProvider getCredentialsProvider(URL serverUrl) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(serverUrl.getHost(), serverUrl.getPort()),
                new UsernamePasswordCredentials(
                        configurationBean.getConfiguration().getUsername(),
                        configurationBean.getDecryptedPassword()
                )
        );
        return credentialsProvider;
    }

    private URI parseServerUri(URL serverUrl) {
        try {
            return new URIBuilder(serverUrl.toURI())
                    .setPath(serverUrl.getPath() + API_PATH)
                    .addParameter("os_authType","basic")
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to parse bamboo server URI.", e);
        }
    }

    public FavouriteBuildResponse getFavoriteBuildStatus() {
        logger.debug("Fetching favourite builds status from Bamboo server.");
        URI uriForFavoritesBuilds = getUriForFavoritesBuilds();
        byte[] entityBytes = getResrponseEntityBytes(uriForFavoritesBuilds);
        return parseResponseIntoModel(entityBytes);
    }

    private FavouriteBuildResponse parseResponseIntoModel(byte[] entityBytes) {
        try {
            return objectMapper.readValue(entityBytes, FavouriteBuildResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed read from response '" + new String(entityBytes) + "' of favourite builds from '" + serverUri + "'", e);
        }
    }

    private byte[] getResrponseEntityBytes(URI uriForFavoritesBuilds) {
        HttpGet httpGet = new HttpGet(uriForFavoritesBuilds);
        CloseableHttpResponse httpResponse = null;
        byte[] entityBytes;
        try {
            httpResponse = httpClient.execute(httpGet);
            validateResponse(httpResponse);
            entityBytes = EntityUtils.toByteArray(httpResponse.getEntity());
        } catch (Exception e) {
            throw new RuntimeException("Failed obtaining of favourite builds from '" + uriForFavoritesBuilds + "'", e);
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
        }
        return entityBytes;
    }

    private void validateResponse(CloseableHttpResponse httpResponse) {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("Invalid http code in Bamboo response - '" + statusCode + " - " + httpResponse.getStatusLine().getReasonPhrase() + "'");
        }
    }


    private URI getUriForFavoritesBuilds() {
        URI uri;
        try {
            uri = new URIBuilder(serverUri)
                    .setPath(serverUri.getPath() + "/result.json")
                    .addParameter("favourite", null)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to build uri for obtain favourite builds from '" + serverUri + "'", e);
        }
        return uri;
    }


    public void closeClient() {
        HttpClientUtils.closeQuietly(httpClient);
    }

}
