import {BalaoInformativo} from "../types/BalaoInformativo";

export const BalaoInformativoService = {
    async listByProcesso(idProcesso: number) {
        console.log(`simulando busca de balões para o processo ${idProcesso}`);
        //Futuramente será algo como: return httpClient.get(`/balao-informativo/por-processo/${idProcesso}`)
        return [];
    },
    async getById(idBalao: number) {
        console.log(`Simulando busca de balão ${idBalao}`);
    },
    async create(balao: BalaoInformativo) {
        console.log('Simulando criação do balão:', balao);
    },
    async update(idBalao: number, balao: BalaoInformativo) {
        console.log(`Simulando atualização do balão ${idBalao}:`, balao);
    },
    async delete(idBalao: number) {
        console.log(`Simulando deleção do balão ${idBalao}`);
    }
};