/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import rockstar.statement.StatementFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import rockstar.statement.Block;
import rockstar.statement.BlockEnd;
import rockstar.statement.Program;
import rockstar.statement.Statement;
import rockstar.statement.ContinuingBlockStatementI;

/**
 *
 * @author Gabor
 */
public class Parser {

    private String filename;
    private BufferedReader rdr;

    public Parser(String filename) throws FileNotFoundException {
        this(new FileInputStream(new File(filename)), filename);
    }

    public Parser(InputStream is, String filename) throws FileNotFoundException {
        this.filename = filename;
        rdr = new BufferedReader(new InputStreamReader(is));
    }

    public Program parse() {
        Program prg = new Program(filename);
        int lnum = 1;

        String line;
        Stack<Block> blocks = new Stack();
        blocks.push(prg);
        try {
            while ((line = rdr.readLine()) != null) {
                Statement stmt = StatementFactory.getStatementFor(new Line(line, filename, lnum));
                if (stmt instanceof BlockEnd) {
                    // simple block closing: no need to add it anywhere
                    if (blocks.size() > 1) {
                        Block finishedBlock = blocks.pop();
                        if (!finishedBlock.blockClosed()) {
                            parseError(finishedBlock.getClass().getSimpleName() + " is not finished properly (1)");
                        }
                    }
                } else {
                    // meaningful statements
                    if (stmt instanceof ContinuingBlockStatementI) {
                        // if it sticks to the previous block, close that block, and append it
                        if (blocks.size() > 1) {
                            Block finishedBlock = blocks.pop();
                            if (!finishedBlock.blockClosed()) {
                                parseError(finishedBlock.getClass().getSimpleName() + " is not finished properly (2)");
                            }
                            boolean applied = ((ContinuingBlockStatementI) stmt).applyBlock(finishedBlock);
                            if (!applied) {
                                parseError(stmt.getClass().getSimpleName() + " cannot be applied to " + finishedBlock.getClass().getSimpleName());
                            }
                        }
                    }

                    // append statement to current block
                    blocks.peek().addStatement(stmt);

                    // open a new block if it's a block statement
                    if (stmt instanceof Block) {
                        blocks.push((Block) stmt);
                    }

                }

                lnum++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (blocks.size() > 1) {
            parseError(blocks.size() + " blocks at the end");
        }
        return prg;
    }

    private void parseError(String msg) {
        System.err.println("parse error: " + msg);
    }

}
