import { Card, Column } from "../types/KanbanTypes";
import { MDXEditorMethods } from "@mdxeditor/editor";


export interface CardElementProps {
    card: Card,
    deleteCard: (columnID: string, cardID: string) => void;
    setShowCreateCardForm: (state: boolean) => void;
    setTempCard: (card: Card) => void;
    setIsEdition: (state: boolean) => void;
    setTempColumnID: (id: string) => void;
}

export interface ColumnContainerProps {
    column: Column;
    deleteColumn: (id: string) => void;
    updateColumnTitle: (id: string, title: string) => void;
    createCard: (columnID: string) => void;
    deleteCard: (columnID: string, cardID: string) => void;
    setShowCreateCardForm: (state: boolean) => void;
    setTempCard: (card: Card) => void;
    setIsEdition: (state: boolean) => void;
    setTempColumnID: (id: string) => void;
}

export interface CreateEditCardProps {
    showCreateCardForm: boolean;
    createCardForm: (event: any, isEdition: boolean) => void;
    card: Card;
    updateListTitle: any;
    handleRemoveInput: any;
    handleRemoveList: any;
    handleAddList: any;
    handleAddInput: any;
    setShowCreateCardForm: any;
    handleInputChange: any;
    handleToggleCheckbox: any;
    isEdition: boolean;
    tags: any;
    addNewTag: any;
    removeCurrentTag: any;
}

export interface RichEditorProps {
    markdown: string
    editorRef?: React.MutableRefObject<MDXEditorMethods | null>
    removeCurrentEditor?: any;
    wrapperKey: any;
}

