export type CheckList = {
    name: string,
    id: string,
    items: CheckListItem[],
}

export type CheckListItem = {
    name: string,
    completed: boolean,
    checklistId: string,
}

export type Card = {
    title: string,
    id: string,
    columnID: string,
    description: string,
    checklists: CheckList[],
    tags: Tag[],
    members: Member[],
}

export type Column = {
    title: string,
    columnType: number,
    id: string,
    cardsList: Card[],
}

export type KanbanData = {
    columns: Column[],
    kanbanId: string,
}

export type Tag = {
    title: string,
    color: string,
    id: string,
}

export type Member = {
    name: string,
    id: string,
}

export type DateValuePiece = Date | null;
export type DateValue = DateValuePiece | [DateValuePiece, DateValuePiece];
