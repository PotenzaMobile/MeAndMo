package com.cipl.meandmo.utils;

import com.ciyashop.library.apicall.URLS;

public class APIS {

    //TODO:Copy and Paste URL and Key Below from Admin Panel.

    public final String APP_URL = "https://meandmo.co.in/";
    public final String WOO_MAIN_URL = APP_URL + "wp-json/wc/v2/";
    public final String MAIN_URL = APP_URL + "wp-json/pgs-woo-api/v1/";

    public static final String CONSUMERKEY = "LJ1B0ShxEZwM";
    public static final String CONSUMERSECRET = "e0aXQPbpPSJwdvycCEQf9Zq2858nEPlFQEGJESmfiofuT4qk";
    public static final String OAUTH_TOKEN = "wzPbkboivV5cPaXJQlVrrLAJ";
    public static final String OAUTH_TOKEN_SECRET = "xmjPWIRXT48Z0a5x1gv8LjZE8h9Y6pyp06MHMxGLJsMzSG5s";

    public static final String WOOCONSUMERKEY = "ck_efc34f6457b56c8adb69212f63b7b49088dd33bc";
    public static final String WOOCONSUMERSECRET = "cs_ec827e7923e394e62dbc77d7f715f0b3ce6605fd";
    public static final String version="4.2.0";
    public static final String purchasekey="75ed9c61-a921-42f8-89f1-c271b12c4874";


    public APIS() {
        URLS.APP_URL = APP_URL;
        URLS.NATIVE_API = APP_URL + "wp-json/wc/v3/";
        URLS.WOO_MAIN_URL = WOO_MAIN_URL;
        URLS.MAIN_URL = MAIN_URL;
        URLS.version = version;
        URLS.CONSUMERKEY = CONSUMERKEY;
        URLS.CONSUMERSECRET = CONSUMERSECRET;
        URLS.OAUTH_TOKEN = OAUTH_TOKEN;
        URLS.OAUTH_TOKEN_SECRET = OAUTH_TOKEN_SECRET;
        URLS.WOOCONSUMERKEY = WOOCONSUMERKEY;
        URLS.WOOCONSUMERSECRET = WOOCONSUMERSECRET;
        URLS.PURCHASE_KEY=purchasekey;
    }
}