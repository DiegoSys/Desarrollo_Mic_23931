export interface Representative {
    name: string;
    image: string;
}

export interface Country {
    name: string;
    code: string;
}

export interface TableData {
    id?: number;
    name: string;
    country: Country;
    representative: Representative;
    status: string;
    verified: boolean;
    date?: Date;
}
