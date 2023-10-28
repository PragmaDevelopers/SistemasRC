import '@mdxeditor/editor/style.css';
import dynamic from 'next/dynamic';
import { toolbarPlugin } from '@mdxeditor/editor/plugins/toolbar';
import {
    headingsPlugin,
    listsPlugin,
    quotePlugin,
    thematicBreakPlugin,
    linkPlugin,
    linkDialogPlugin,
    imagePlugin,
    tablePlugin,
    markdownShortcutPlugin,
    UndoRedo,
    BoldItalicUnderlineToggles,
    BlockTypeSelect,
    InsertImage,
    InsertTable,
    ListsToggle,
    CreateLink,
    ButtonOrDropdownButton,
    directivesPluginHooks,
} from "@mdxeditor/editor";
import {
    RichEditorProps
} from '@/app/interfaces/KanbanInterfaces';
import { Bars3Icon } from '@heroicons/react/24/solid';

const MDXEditor = dynamic(
    () => import('@mdxeditor/editor/MDXEditor').then((mod) => mod.MDXEditor),
    { ssr: false }
);

function AlignTextMDXToolbarItem() {
    const insertDirective = directivesPluginHooks.usePublisher('insertDirective');
    const items = [
        { value: 'LEFT', label: 'Align Left' },
        { value: 'CENTER', label: 'Align Center' },
        { value: 'RIGHT', label: 'Align Right' },
    ]
    return (
        <ButtonOrDropdownButton
            title='Align Text'
            onChoose={(alignName) => {
                insertDirective({
                    type: 'containerDirective',
                    name: alignName,
                });
            }}
            items={items}
        >
            <Bars3Icon className='aspect-square w-6' />
        </ButtonOrDropdownButton>
    );
}

function RichEditor(props: RichEditorProps) {
    return (
        <MDXEditor
            className="MDXEditor"
            ref={props?.editorRef}
            markdown={props.markdown != undefined ? props?.markdown : ""}
            plugins={[
                headingsPlugin(),
                listsPlugin(),
                quotePlugin(),
                thematicBreakPlugin(),
                linkPlugin(),
                linkDialogPlugin(),
                imagePlugin(),
                tablePlugin(),
                markdownShortcutPlugin(),
                toolbarPlugin({
                    toolbarContents: () => (<>
                        <UndoRedo />
                        <BlockTypeSelect />
                        <BoldItalicUnderlineToggles />
                        <InsertImage />
                        <InsertTable />
                        <ListsToggle />
                        <CreateLink />
                        <AlignTextMDXToolbarItem />
                    </>
                    )
                }),
            ]}

        />
    );
}

export default function Page() {

    return (
        <main className="w-full h-full bg-neutral-50">
            <RichEditor markdown={"# MarkDown"} />
        </main>
    );
}
