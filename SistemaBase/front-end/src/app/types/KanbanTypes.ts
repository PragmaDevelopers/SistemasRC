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