//import {environment} from "./src/environments/environment";

const PROXY_CONFIG = [
  {
    context: [
      "/ui"
      // "/api"
    ],
    target: 'http://localhost:8080', //environment.apiUrl,
    secure: false,
    logLevel: "debug"
  }
];

module.exports = PROXY_CONFIG;
