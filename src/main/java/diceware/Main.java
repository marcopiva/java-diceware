package diceware;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.CharStreams;
import com.google.common.primitives.Ints;

public class Main {

    public static void main(String[] args) throws Exception {
    	
    	Integer words = null;
    	
    	if (args.length == 0 || ((words = Ints.tryParse(args[0])) == null)) {
    			words = 10;
    	}

    	Map<String, String> map = new HashMap<String, String>();
//    	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("word_list.txt");
    	InputStream is = Main.class.getClass().getResourceAsStream("/word_list.txt");
    	
    	String stringFromStream = CharStreams.toString(new InputStreamReader(is, "UTF-8"));
    	
    	Iterable<String> lines = Splitter.on(CharMatcher.is('\n')).split(stringFromStream);
    	
//    	System.out.println(stringFromStream);


//List<String> lines = Files.readLines(file, Charsets.UTF_8);
    	
    	for (String item : lines) {
    		Iterable<String> array = Splitter.on(CharMatcher.BREAKING_WHITESPACE).split(item);
    		Iterator<String> it = array.iterator();
    		String chiave = it.next();
    		String valore = it.next();
    		map.put(chiave, valore);
    	}


    	DefaultHttpClient httpclient = new DefaultHttpClient();
        try {

            URIBuilder builder = new URIBuilder();
            builder.setScheme("http").setHost("www.random.org").setPath("/integers")
                .setParameter("num", String.valueOf(5 * words))
                .setParameter("min", "1")
                .setParameter("max", "6")
                .setParameter("col", "1")
                .setParameter("base", "10")
                .setParameter("format", "plain")
                .setParameter("rnd", "new");
            URI uri = builder.build();
            
            HttpGet httpget = new HttpGet(uri);
            
        	httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope("", 3128),
                    new UsernamePasswordCredentials("", ""));
            HttpHost proxy = new HttpHost("", 3128);

            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);


            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            
            responseBody = CharMatcher.BREAKING_WHITESPACE.removeFrom(responseBody);
            
            Iterable<String> arrayresponseBody = Splitter.fixedLength(5).split(responseBody);
            
            List<String> parole = new ArrayList<String>();

            for (String item : arrayresponseBody) {
            		parole.add(item);
            }
            List<String> output = new ArrayList<String>();
            for (String item : parole) {
            	output.add(map.get(item));
            }
            
            Joiner joiner = Joiner.on(" ");

            System.out.println(joiner.join(output));

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
    	
    }


