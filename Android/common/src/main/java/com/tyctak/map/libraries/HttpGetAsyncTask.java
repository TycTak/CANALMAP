package com.tyctak.map.libraries;

//public class HttpGetAsyncTask extends AsyncTask<String, Void, Void> {
//    // This is the JSON body of the post
//    JSONObject postData;
//    // This is a constructor that allows you to pass in the JSON body
//    public HttpGetAsyncTask(Map<String, String> postData) {
//        if (postData != null) {
//            this.postData = new JSONObject(postData);
//        }
//    }
//
//    // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
//    @Override
//    protected Void doInBackground(String... params) {
//
//        try {
//            // This is getting the url from the string we passed in
//            URL url = new URL(params[0]);
//
//            // Create the urlConnection
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//            urlConnection.setDoInput(true);
//            urlConnection.setDoOutput(true);
//
//            urlConnection.setRequestProperty("Content-Type", "application/json");
//
//            urlConnection.setRequestMethod("POST");
//
//
//            // OPTIONAL - Sets an authorization header
//            urlConnection.setRequestProperty("Authorization", "someAuthString");
//
//            // Send the post body
//            if (this.postData != null) {
//                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
//                writer.write(postData.toString());
//                writer.flush();
//            }
//
//            int statusCode = urlConnection.getResponseCode();
//
//            if (statusCode ==  200) {
//
//                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
//
//                String response = convertInputStreamToString(inputStream);
//
//                // From here you can convert the string to JSON with whatever JSON parser you like to use
//                // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
//            } else {
//                // Status code is not 200
//                // Do something to handle the error
//            }
//
//        } catch (Exception e) {
////            Log.d(TAG, e.getLocalizedMessage());
//        }
//        return null;
//    }
//}