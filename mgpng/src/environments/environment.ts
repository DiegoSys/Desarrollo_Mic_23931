export const environment = {
    production: false,
    appApiUrl: 'http://localhost:8081/api/v1.0',

    userInfomation: 'https://api-ufa.espe.edu.ec/api/v1.0/private/user/personal-information',
    menu: 'https://users-api.espe.edu.ec/adm/id/code/',
    sso: {
        serverUrl: 'https://srvcas.espe.edu.ec',
        clientId: 'F_13VTNPdVHPSstUZtYmldfl2UYa',
        requireHttps: false,
        issuer: '/oauth2endpoints/token',
        redirectUri: window.location.origin,
        postredirectUrL: window.location.origin,
        scope: 'openid profile email',
        logout: '/oidc/logout',
        tokenEndpoint: '/oauth2endpoints/token',
        userinfoEndpoint: '/oauth2/userinfo',
        authorizationEndpoint: '/oauth2/authorize',
        jwksEndpoint: '/oauth2/jwks',
        showDebugInformation: true,
        responseType: 'id_token token',
    },
};
