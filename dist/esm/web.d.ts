import { WebPlugin } from '@capacitor/core';
import type { Contact, ContactsPlugin, Group, PermissionStatus } from './definitions';
export declare class ContactsWeb extends WebPlugin implements ContactsPlugin {
    getPermissions(): Promise<PermissionStatus>;
    checkPermissions(): Promise<PermissionStatus>;
    requestPermissions(): Promise<PermissionStatus>;
    getContacts(): Promise<{
        contacts: Contact[];
    }>;
    createContact(_data: {
        nam?: string;
        number: string;
    }): Promise<void>;
    addToExistingContact(_data: {
        nam?: string;
        number: string;
    }): Promise<void>;
    deleteContact(_data: {
        contactId: string;
    }): Promise<void>;
    getGroups(): Promise<{
        groups: Group[];
    }>;
    getContactGroups(): Promise<{
        [key: string]: Group[];
    }>;
}
