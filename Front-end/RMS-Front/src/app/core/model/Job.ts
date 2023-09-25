export interface JobModel{
    id?: number ;
    title: string ;
    description?: string ;
    filename?: string ;
    createdAt?: Date| number ;
    updatedAt?: Date| number ;
    active?: boolean ;
}

export interface JobList{
    contents: JobModel[];
    page?: number;
    pageSize?: number;
    total?: number;
}
