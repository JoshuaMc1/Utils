package com.example.app.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import com.example.app.Utils.Interfaces.DialogCallback;

class Utils {
    /**
     * Shows a short message on the screen at the position of the given view.
     *
     * @param view The view that the message will be shown at.
     * @param msj  The message to be shown.
     */
    void message(View view, String msj) {
        Snackbar.make(view, msj, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Concatenates the given arguments into a single string with a space separator.
     *
     * @param args The objects to concatenate.
     * @return A single string with all arguments concatenated and separated by
     *         spaces.
     */
    String concat(@NonNull Object... args) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            result.append(args[i]);

            if (i < args.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    /**
     * Concatenates the given arguments into a single string without a space
     * separator.
     *
     * @param args The objects to concatenate.
     * @return A single string with all arguments concatenated and without spaces.
     */
    String contactWithoutSpace(@NonNull Object... args) {
        StringBuilder result = new StringBuilder();

        for (Object arg : args) {
            result.append(arg);
        }

        return result.toString();
    }

    /**
     * Clears the text of all EditTexts contained in the given ViewGroup.
     * This method works recursively and will clear all EditTexts in the given
     * ViewGroup and its descendants.
     *
     * @param viewGroup The ViewGroup to search for EditTexts in.
     */
    void clearInputs(@NonNull ViewGroup viewGroup) {
        Queue<View> viewQueue = new LinkedList<>();
        viewQueue.add(viewGroup);

        while (!viewQueue.isEmpty()) {
            View currentView = viewQueue.poll();

            if (currentView instanceof EditText) {
                ((EditText) currentView).setText("");
            } else if (currentView instanceof ViewGroup) {
                ViewGroup currentGroup = (ViewGroup) currentView;

                for (int i = 0; i < currentGroup.getChildCount(); i++) {
                    viewQueue.add(currentGroup.getChildAt(i));
                }
            }
        }
    }

    /**
     * Clears the text of the given EditTexts. The EditTexts are cleared only if
     * they are not null.
     * This method requires API level 24 or higher.
     *
     * @param args The EditTexts to be cleared.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    void customClearInputs(@NonNull EditText... args) {
        Arrays.stream(args).filter(Objects::nonNull).forEach(editText -> editText.setText(""));
    }

    /**
     * Creates a new JSONObject from the given arguments. The arguments must be
     * given in key-value pairs. The supported data types for the values are
     * String, Integer, Double, and Boolean.
     *
     * @param args The key-value pairs to be added to the JSONObject. The number
     *             of arguments must be even.
     * @return A new JSONObject containing all key-value pairs given in the
     *         arguments.
     * @throws IllegalArgumentException If the number of arguments is not even or
     *                                  if the data type of a value is not
     *                                  supported.
     */
    JSONObject createJson(@NonNull Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("The number of arguments must be even.");
        }

        JSONObject json = new JSONObject();

        try {
            for (int i = 0; i < args.length; i += 2) {
                String key = String.valueOf(args[i]);
                Object value = args[i + 1];

                if (value instanceof String || value instanceof Integer || value instanceof Double
                        || value instanceof Boolean) {
                    json.put(key, value);
                } else {
                    throw new IllegalArgumentException("Unsupported data type: " + value.getClass());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Shows a notification with the given title and message. The notification is
     * shown on API level 26 or higher. The notification is shown with maximum
     * importance and priority. The notification is also grouped with other
     * notifications from this application.
     *
     * @param context The context to get the system service from.
     * @param title   The title of the notification.
     * @param message The message of the notification.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    void showNotification(@NonNull Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = "Application";
            int importance = NotificationManager.IMPORTANCE_MAX;

            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.app_name), name,
                    importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                context.getString(R.string.app_name)).setContentTitle(title).setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher).setPriority(NotificationCompat.PRIORITY_MAX)
                .setGroup(contactWithoutSpace(R.string.app_name, "notifications"))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        notificationManager.notify(32, builder.build());
    }

    /**
     * Displays a simple toast notification with the given message.
     *
     * @param context The context from which the Toast is shown.
     * @param msg     The message to be displayed in the Toast.
     */
    void simpleNotification(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Checks if the device is connected to the internet.
     *
     * @param context The context to use to get the system service.
     * @return True if the device is connected to the internet. False otherwise.
     */
    boolean isInternetConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    /**
     * Reads the given input stream and returns its contents as a byte array.
     *
     * @param is The input stream to read from.
     * @return The contents of the input stream as a byte array.
     * @throws IOException If any error occurs while reading the input stream.
     */
    @NonNull
    byte[] getBytes(@NonNull InputStream is) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024 * 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = is.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    /**
     * Creates a temporary image file in the default picture directory with the
     * given filename prefix and ".png" extension.
     *
     * @param ctx The context to use to get the external file directory.
     * @return A new image file in the default picture directory.
     * @throws IOException If the file could not be created.
     */
    @NonNull
    File createImageFile(@NonNull Context ctx) throws IOException {
        String imageFileName = generateImageName();
        File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".png", storageDir);
    }

    /**
     * Generates a filename for an image file in the following format:
     * "PNG_<date>_<time>_<random>.png"
     *
     * @return A filename for an image file.
     */
    String generateImageName() {
        return contactWithoutSpace("PNG_",
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()), "_");
    }

    /**
     * Returns the current date formatted according to the specified format.
     *
     * @param format The format string used to format the date.
     * @return A string representing the current date in the specified format.
     */
    String getCurrentDate(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }

    /**
     * Formats the given timestamp according to the specified format.
     *
     * @param timestamp The timestamp to format.
     * @param format    The format string used to format the timestamp.
     * @return A string representing the timestamp in the specified format.
     */
    String formatTimestamp(long timestamp, String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date(timestamp));
    }

    /**
     * Capitalizes the first letter of the given string and makes all other
     * letters lower case.
     *
     * @param input The string to capitalize.
     * @return A new string with the first letter capitalized and all other
     *         letters lower case.
     */
    String capitalize(String input) {
        if (input == null || input.isEmpty())
            return input;

        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    /**
     * Reverses the given string.
     *
     * @param input The string to reverse.
     * @return The reversed string.
     */
    String reverseString(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * Deletes all files in the specified directory.
     *
     * @param dir The directory from which to delete files.
     * @return True if all files were successfully deleted. False otherwise.
     */
    boolean deleteFilesInDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (!file.delete())
                    return false;
            }
        }

        return true;
    }

    /**
     * Reads the contents of the given file and returns it as a string.
     *
     * @param file The file to read from.
     * @return The contents of the file as a string.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    String readFileToString(File file) throws IOException {
        StringBuilder text = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
        }

        return text.toString().trim();
    }

    /**
     * Encodes the given string into a Base64 string.
     *
     * @param input The string to be encoded.
     * @return The Base64 encoded string.
     */
    String encryptBase64(String input) {
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    /**
     * Decrypts a base64 encoded string back to its original string.
     *
     * @param encoded The base64 encoded string to decrypt.
     * @return The decrypted string.
     */
    String decryptBase64(String encoded) {
        return new String(Base64.decode(encoded, Base64.DEFAULT));
    }

    /**
     * Retrieves the device name in the format "Model (Manufacturer)".
     *
     * @return A string representing the device name.
     */
    String getDeviceName() {
        return Build.MODEL + " (" + Build.MANUFACTURER + ")";
    }

    /**
     * Checks if dark mode is enabled on the device.
     *
     * @param context The context to use to get the system service.
     * @return True if dark mode is enabled. False otherwise.
     */
    boolean isDarkModeEnabled(@NonNull Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Retrieves the screen resolution of the device in the format "widthxheight".
     *
     * @param context The context to use to get the system service.
     * @return A string representing the screen resolution of the device.
     */
    String getScreenResolution(@NonNull Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels + "x" + metrics.heightPixels;
    }

    /**
     * Closes the soft keyboard if it is currently open.
     *
     * @param context The context to use to get the system service.
     * @param view    The view associated with the keyboard to be closed.
     */
    void closeKeyboard(@NonNull Context context, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Vibrates the device for the given amount of time.
     *
     * @param context      The context to use to get the system service.
     * @param milliseconds The length of time in milliseconds to vibrate the device.
     */
    void vibrateDevice(@NonNull Context context, long milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(milliseconds);
        }
    }

    /**
     * Resizes a given bitmap to the given width and height, using the best
     * filtering available. The resized bitmap will be returned.
     *
     * @param bitmap    The bitmap to be resized.
     * @param newWidth  The new width of the bitmap.
     * @param newHeight The new height of the bitmap.
     * @return The resized bitmap.
     */
    Bitmap resizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Converts the given bitmap to a byte array using the PNG format with a
     * compression quality of 100.
     *
     * @param bitmap The bitmap to convert.
     * @return The byte array representation of the bitmap.
     */
    byte[] bitmapToByteArray(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Converts the given byte array to a bitmap using the BitmapFactory.
     *
     * @param bytes The byte array to convert.
     * @return The bitmap representation of the byte array.
     */
    Bitmap byteArrayToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Converts the given JSONObject into a string that is formatted for
     * human-readability. The string will be indented with four spaces for
     * each level of nesting.
     *
     * @param json The JSONObject to format.
     * @return The formatted string, or "Invalid JSON" if the object is
     *         invalid.
     */
    String prettyPrintJson(@NonNull JSONObject json) {
        try {
            return json.toString(4);
        } catch (JSONException e) {
            return "Invalid JSON";
        }
    }

    /**
     * Executes the given task and measures the time it takes to execute in
     * nanoseconds. The result is logged to the console with the tag
     * "ExecutionTime".
     *
     * @param task The task to measure.
     */
    void measureExecutionTime(@NonNull Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();

        Log.d("ExecutionTime", "Execution took: " + (endTime - startTime) + " ns");
    }

    /**
     * Removes duplicate elements from the given list and returns a new list
     * containing only unique elements. The order of elements in the resulting
     * list is not guaranteed to be the same as in the original list.
     *
     * @param list The list from which to remove duplicate elements.
     * @param <T>  The type of elements in the list.
     * @return A new list containing only unique elements from the original list.
     */
    <T> List<T> removeDuplicates(List<T> list) {
        return new ArrayList<>(new HashSet<>(list));
    }

    /**
     * Computes the intersection of two lists and returns a new list containing
     * only those elements that are present in both lists. The order of elements
     * in the resulting list is not guaranteed to be the same as in the original
     * lists.
     *
     * @param list1 The first list.
     * @param list2 The second list.
     * @param <T>   The type of elements in the lists.
     * @return A new list containing only the elements that are present in both
     *         lists.
     */
    <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> intersection = new ArrayList<>(list1);
        intersection.retainAll(list2);
        return intersection;
    }

    /**
     * Converts the given array to a list and returns it. The order of elements
     * in the resulting list is the same as in the original array.
     *
     * @param array The array to convert.
     * @param <T>   The type of elements in the array.
     * @return A new list containing all elements from the original array.
     */
    <T> List<T> arrayToList(T[] array) {
        return Arrays.asList(array);
    }

    /**
     * Converts the given size in bytes to a human-readable format, with one
     * decimal place of precision. The returned string will be in the format
     * "X.XXX YYY", where X is the number of whole units, YYY is the unit of
     * measurement (B, KB, MB, GB, TB), and the number of decimal places is
     * one. If the size is zero or negative, the returned string will be "0 B".
     *
     * @param size The size in bytes to convert.
     * @return A human-readable string representing the size.
     */
    String getReadableFileSize(long size) {
        if (size <= 0)
            return "0 B";

        final String[] units = { "B", "KB", "MB", "GB", "TB" };

        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Saves the given text to the given file. This method will overwrite any
     * existing file with the same name. If the file could not be written to (for
     * example, if the file is a directory, or if the file is not writable), an
     * IOException will be thrown. The file will be written in the default
     * character encoding of the system.
     *
     * @param text The text to be saved to the file.
     * @param file The file to write the text to.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    void saveTextToFile(String text, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(text);
        }
    }

    /**
     * Returns the color value from the given resource id. This method is
     * compatible with all API levels and will not throw an exception if the
     * resource could not be found.
     *
     * @param context  The context to use to get the system service.
     * @param colorRes The resource id of the color to get.
     * @return The color value from the given resource id.
     */
    int getColorSafe(@NonNull Context context, int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    /**
     * Returns a drawable object associated with a particular resource ID. This
     * method
     * is compatible with all API levels and will not throw an exception if the
     * resource
     * could not be found.
     *
     * @param context     The context to use to get the drawable resource.
     * @param drawableRes The resource ID of the drawable to retrieve.
     * @return The drawable object associated with the specified resource ID, or
     *         null
     *         if the resource could not be found.
     */
    Drawable getDrawableSafe(@NonNull Context context, int drawableRes) {
        return ContextCompat.getDrawable(context, drawableRes);
    }

    /**
     * Shows a confirmation dialog with the given title and message. The dialog
     * contains two buttons, "Confirm" and "Cancel". The callback is invoked with
     * the boolean argument true if the user clicks "Confirm", and false
     * otherwise.
     *
     * @param context  The context to use to show the dialog.
     * @param title    The title of the dialog.
     * @param message  The message of the dialog.
     * @param callback The callback to invoke after the user has made a
     *                 selection.
     */
    void showConfirmationDialog(Context context, String title, String message, DialogCallback callback) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton("Confirm", (d, which) -> callback.onResult(true))
                .setNegativeButton("Cancel", (d, which) -> callback.onResult(false)).create();

        dialog.show();
    }

    /**
     * Shows a confirmation dialog with the given title and message. The dialog
     * contains two buttons, "Confirm" and "Cancel". The returned future is
     * completed with true if the user clicks "Confirm", and false otherwise.
     *
     * @param context The context to use to show the dialog.
     * @param title   The title of the dialog.
     * @param message The message of the dialog.
     * @return A future that is completed with the user's selection.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    CompletableFuture<Boolean> showConfirmationDialog(Context context, String title, String message) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton("Confirm", (d, which) -> future.complete(true))
                .setNegativeButton("Cancel", (d, which) -> future.complete(false)).create();

        dialog.show();

        return future;
    }

    /**
     * Fades in the given view by setting its visibility to VISIBLE and then
     * animating its alpha from 0 to 1 over the given duration.
     *
     * @param view     The view to fade in.
     * @param duration The duration of the fade in animation in milliseconds.
     */
    void fadeInView(@NonNull View view, long duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(duration).start();
    }

    /**
     * Opens the given URL in the default browser. This method will throw an
     * ActivityNotFoundException if no suitable activity could be found to
     * handle the given URL.
     *
     * @param context The context to use to open the URL.
     * @param url     The URL to open.
     */
    void openUrl(@NonNull Context context, @NonNull String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * Shares the given text with other applications. The text is shared as a
     * plain text type and can be received by any application that supports
     * receiving text types.
     *
     * @param context The context to use to share the text.
     * @param text    The text to share. This text will be sent to the
     *                application that is chosen by the user to share the text
     *                with.
     */
    void shareText(@NonNull Context context, @NonNull String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
