import Axios from 'axios';

declare const env: {
    HTTP_BASE_URL?: string;
};

export const httpClient = Axios.create({
    baseURL: env?.HTTP_BASE_URL || 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});