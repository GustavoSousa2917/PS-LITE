export interface BalaoInformativo {
    id?: number; //opcional (pois não temos o ID antes de criar o balão no banco de dados)
    titulo: string;
    mensagem: string;
    processoSeletivo: {
        id: number;
    }
}