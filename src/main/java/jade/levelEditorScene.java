package jade;


import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class levelEditorScene extends Scene{

private String vertexShaderSrc ="#version 330 core\n" +
        "\n" +
        "    layout (location =0) in vec3 aPos;\n" +
        "    layout (location =1) in vec4 aColor;\n" +
        "\n" +
        "    out vec4 fColor;\n" +
        "\n" +
        "    void main(){\n" +
        "        fColor = aColor;\n" +
        "        gl_Position = vec4(aPos, 1.0);\n" +
        "    }";

private String fragmentShaderSrc ="#version 330 core\n" +
        "\n" +
        "in vec4 fColor;\n" +
        "\n" +
        "out vec4 color;\n" +
        "\n" +
        "void main(){\n" +
        "    color = fColor;\n" +
        "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
            //position      //color
            0.5f,  -0.5f, 0.0f,         1.0f, 0.0f, 0.0f, 1.0f,  //bottom right 0
            -0.5f, 0.5f, 0.0f,          0.0f, 1.0f, 0.0f, 1.0f, //top left 1
            0.5f, 0.5f, 0.0f,           0.0f, 0.0f, 1.0f, 1.0f, //top right 2
            -0.5f, -0.5f, 0.0f,          1.0f, 1.0f, 0.0f, 1.0f, //bottom left 3

    };

    // IMPORTANT: must be in counter clockwise order
    private int[] elementArray ={
            2,1,0, // top right triangle
            0,1,3 //bottom left triangle

    };

    private int vaoID, vboID, eboID;


    public levelEditorScene(){
    }
    @Override
    public void init(){
        // Compile and link shaders

        // first load and compile the vertex shader

        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass teh shader source to the gpu
        glShaderSource(vertexID, vertexShaderSrc );
        glCompileShader(vertexID);

        // check for errors in compilation process
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultshader.glsl \n\t vertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }
        // first load and compile the vertex shader

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass teh shader source to the gpu
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        // check for errors in compilation process
         success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultshader.glsl \n\t fragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }
        // link shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        //check linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE){
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultshader.glsl \n\t linking of shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }
        // generate vao, vbo,
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBo upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // add the vertex attribute pointers
        int poistionsSize = 3;
        int colorSize =4;
        int floatSizeBytes =4;
        int vertexSizeBytes = (poistionsSize + colorSize )* floatSizeBytes;
        glVertexAttribPointer(0, poistionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, poistionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

    }
    @Override
    public void update(float dt) {

            //bind shader program
        glUseProgram(shaderProgram);
        //bind the vao that we are using
        glBindVertexArray(vaoID);

        // enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);

    }


}
