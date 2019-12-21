src = $(wildcard *.c)
obj = $(src:.c=.o)

p4rezServer: $(obj)
	gcc -o $@ $^ 

.PHONY: clean
clean:
	rm -f $(obj) p4rezServer* *.o