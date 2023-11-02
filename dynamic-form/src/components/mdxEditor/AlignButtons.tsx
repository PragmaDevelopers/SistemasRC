import { Button, JsxEditorProps, corePluginHooks,JsxComponentDescriptor,NestedLexicalEditor, jsxPluginHooks, diffSourcePluginHooks } from "@mdxeditor/editor"
import { $createRangeSelection, RangeSelection } from "lexical";
  
// a toolbar button that will insert a JSX element into the editor.
export const AlignCenterButton = () => {

const setMarkdown = corePluginHooks.usePublisher("setMarkdown");
const getMarkdown = corePluginHooks.useEmitterValues("markdown");

return (
    <Button
    title="Center Align"
    onClick={() =>{
        const lines = getMarkdown[0].split("\n\n");
        const lineIndex = Number(sessionStorage.getItem("edit_pdf_line_selected")?.split("-")[1]);
        const line = lines[lineIndex]?.trim();
        if(line){
            console.log(lines)
            if(!line.match(/^<u>[\s]*<\/u>$/) && !line.match(/^(\| ([\s\S]*?) \|)|([\s\S]*?) \|$/g) && ![undefined,"","&#x20;","****&#x20;","******&#x20;"].includes(line)){
                //LEFT TO CENTER
                lines.splice(lineIndex,1,`| ${line} |`)
                setMarkdown(lines.join("\n\n"))
            }else if(line.match(/^\| ([\s\S]*?) \|$/g)){
                //CENTER TO LEFT
                const newLine = line.replace(/\| ([\s\S]*?) \|/g,"$1")
                lines.splice(lineIndex,1,newLine)
                console.log(newLine)
                setMarkdown(lines.join("\n\n"))
            }else if(line.match(/^([\s\S]*?) \|$/g)){
                //RIGHT TO CENTER
                const newLine = line.replace(/^([\s\S]*?) \|$/g,"| $1 |");
                console.log(newLine)
                lines.splice(lineIndex,1,newLine)
                setMarkdown(lines.join("\n\n"))
            }
        }
    }}
    >
    =
    </Button>
)
}

export const AlignRightButton = () => {

const setMarkdown = corePluginHooks.usePublisher("setMarkdown");
const getMarkdown = corePluginHooks.useEmitterValues("markdown");

return (
    <Button
    title="Right Align"
    onClick={() =>{
        const lines = getMarkdown[0].split("\n\n");
        const lineIndex = Number(sessionStorage.getItem("edit_pdf_line_selected")?.split("-")[1]);
        const line = lines[lineIndex]?.trim();
        if(line){
            if(!line.match(/^<u>[\s]*<\/u>$/) && !line.match(/^([\s\S]*?) \||\| ([\s\S]*?) \|$/g) && ![undefined,"","&#x20;","****&#x20;","******&#x20;"].includes(line)){
                //LEFT TO RIGHT
                lines.splice(lineIndex,1,`${line} |`)
                setMarkdown(lines.join("\n\n"))
            }else if(line.match(/^\| ([\s\S]*?) \|$/g)){
                //CENTER TO RIGHT
                const newLine = line.replace(/^\| ([\s\S]*?) \|$/g,"$1 \|");
                lines.splice(lineIndex,1,newLine)
                setMarkdown(lines.join("\n\n"))
            }else if(line.match(/^([\s\S]*?) \|$/g)){
                //RIGHT TO LEFT
                const newLine = line.replace(/([\s\S]*?) \|/g,"$1");
                lines.splice(lineIndex,1,newLine)
                setMarkdown(lines.join("\n\n"))
            }
        }
    }}
    >
    &#62;
    </Button>
)
}