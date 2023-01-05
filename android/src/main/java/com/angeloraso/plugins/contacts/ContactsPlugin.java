package com.angeloraso.plugins.contacts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import androidx.activity.result.ActivityResult;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.util.Map;
import java.util.Set;

@CapacitorPlugin(
    name = "Contacts",
    permissions = { @Permission(alias = "contacts", strings = { Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS }) }
)
public class ContactsPlugin extends Plugin {

    private Contacts contacts;

    public void load() {
        Context context = getContext();
        contacts = new Contacts(context);
    }

    @PluginMethod
    @Deprecated
    public void getPermissions(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        JSObject res = new JSObject();
        res.put("granted", contactsPermissionIsGranted());
        call.resolve(res);
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        JSObject res = new JSObject();
        res.put("granted", contactsPermissionIsGranted());
        call.resolve(res);
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        if (contactsPermissionIsGranted()) {
            JSObject res = new JSObject();
            res.put("granted", true);
            call.resolve(res);
        } else {
            requestPermissionForAlias("contacts", call, "contactsPermissionCallback");
        }
    }

    @PermissionCallback
    private void contactsPermissionCallback(PluginCall call) {
        getPermissions(call);
    }

    @PluginMethod
    public void getContacts(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        JSArray jsContacts = contacts.getContacts();
        JSObject res = new JSObject();
        res.put("contacts", jsContacts);
        call.resolve(res);
    }

    @PluginMethod
    public void createContact(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        if (!call.hasOption("number")) {
            call.reject("The number is required");
            return;
        }

        String contactNumber = call.getString("number");
        String contactName;
        if (!call.hasOption("name")) {
            contactName = "";
        } else {
            contactName = call.getString("name");
        }

        Intent intent = new Intent("android.intent.action.CREATE_CONTACT_ACTIVITY");
        intent.setPackage(getContext().getPackageName());

        intent.putExtra("number", contactNumber);
        if (call.hasOption("name")) {
            intent.putExtra("name", contactName);
        }

        startActivityForResult(call, intent, "createContactResult");
    }

    @ActivityCallback
    private void createContactResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }

        if (result.getData() != null) {
            call.resolve();
        } else {
            call.reject("Contact was not saved");
        }
    }

    @PluginMethod
    public void addToExistingContact(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        if (!call.hasOption("number")) {
            call.reject("The number is required");
            return;
        }

        Intent intent = new Intent("android.intent.action.ADD_TO_EXISTING_CONTACT_ACTIVITY");
        intent.setPackage(getContext().getPackageName());

        intent.putExtra("number", call.getString("number"));

        startActivityForResult(call, intent, "addToExistingContactResult");
    }

    @ActivityCallback
    private void addToExistingContactResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }

        if (result.getData() != null) {
            call.resolve();
        } else {
            call.reject("Contact was not saved");
        }
    }

    @PluginMethod
    public void getGroups(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        JSArray groups = contacts.getGroups();
        JSObject res = new JSObject();
        res.put("groups", groups);
        call.resolve(res);
    }

    @PluginMethod
    public void getContactGroups(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        if (!call.hasOption("number")) {
            call.reject("The number is required");
            return;
        }

        Map<String, Set<String>> contactsGroup = contacts.getContactGroups();
        JSObject result = new JSObject();
        for (Map.Entry<String, Set<String>> entry : contactsGroup.entrySet()) {
            JSArray jsGroups = new JSArray();
            Set<String> groups = entry.getValue();
            for (String group : groups) {
                jsGroups.put(group);
            }
            result.put(entry.getKey(), jsGroups);
        }

        call.resolve(result);
    }

    @PluginMethod
    public void deleteContact(PluginCall call) {
        String contactId;
        if (call.hasOption("contactId")) {
            contactId = call.getString("contactId");
        } else {
            call.reject("The contact id is required");
            return;
        }

        contacts.deleteContact(contactId);
        call.resolve();
    }

    private boolean contactsPermissionIsGranted() {
        return getPermissionState("contacts") == PermissionState.GRANTED;
    }
}
