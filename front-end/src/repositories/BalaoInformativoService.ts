import type { BalaoInformativo } from '../types/BalaoInformativo';
import HttpClient from './adapters/HttpClient';

export const BalaoInformativoService = {

    async listByProcesso(idProcesso: number): Promise<BalaoInformativo[]> {
        const response = await HttpClient.get(`/balao-informativo/por-processo/${idProcesso}`);
        return response.data;
    },

    async getById(idBalao: number): Promise<BalaoInformativo> {
        const response = await HttpClient.get(`/balao-informativo/${idBalao}`);
        return response.data;
    },

    async create(balao: BalaoInformativo): Promise<void> {
        await HttpClient.post('/balao-informativo', balao);
    },

    async update(idBalao: number, balao: BalaoInformativo): Promise<void> {
        await HttpClient.put(`/balao-informativo/${idBalao}`, balao);
    },

    async delete(idBalao: number): Promise<void> {
        await HttpClient.delete(`/balao-informativo/${idBalao}`);
    }
};