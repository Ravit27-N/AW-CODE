export interface IDelegate {
    aggregate(data: unknown): Promise<any>;
    count(data: unknown): Promise<any>;
    create(data: unknown): Promise<any>;
    delete(data: unknown): Promise<any>;
    deleteMany(data: unknown): Promise<any>;
    findFirst(data: unknown): Promise<any>;
    findMany(data?: unknown): Promise<any>;
    findUnique(data: unknown): Promise<any>;
    update(data: unknown): Promise<any>;
    updateMany(data: unknown): Promise<any>;
    upsert(data: unknown): Promise<any>;
}
